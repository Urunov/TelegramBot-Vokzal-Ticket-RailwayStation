package com.urunov.telgbot.service;

import com.urunov.telgbot.TelgbotApplication;
import com.urunov.telgbot.model.Car;
import com.urunov.telgbot.model.Train;
import com.urunov.telgbot.model.UserTicketsSubscription;
import com.vdurmont.emoji.Emoji;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Slf4j
@Service
public class UserSubscriptionProcessService {

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private UserTicketsSubscriptionService subscriptionService;
    private TrainTicketsGetInfoService trainTicketsGetInfoService;
    private StationCodeService stationCodeService;
    private CarsProcessingService carsProssingService;
    private ReplyMessagesService messagesService;
    private TelgbotApplication telgbotApplication;

    public UserSubscriptionProcessService(UserTicketsSubscriptionService subscriptionService, TrainTicketsGetInfoService trainTicketsGetInfoService, StationCodeService stationCodeService,
                                          CarsProcessingService carsProssingService, ReplyMessagesService messagesService,
                                          @Lazy TelgbotApplication telgbotApplication) {
        this.subscriptionService = subscriptionService;
        this.trainTicketsGetInfoService = trainTicketsGetInfoService;
        this.stationCodeService = stationCodeService;
        this.carsProssingService = carsProssingService;
        this.messagesService = messagesService;
        this.telgbotApplication = telgbotApplication;
    }

    @Scheduled(fixedRateString = "${subscriptions.processPeriod}")
    public void processAllUsersSubscriptions(){
        log.info("Выполняю обработку подписок пользователей / it is processing user subscriptions.");
        subscriptionService.getAllSubscriptions().forEach(this::processSubscription);
        log.info("Завершил обработку подписок пользователей./ Complied");

    }
    /**
     * Получает актуальные данные по билетам для текущей подписки,
     * если цена изменилась сохраняет последнюю и уведомляет клиента.
     * @return
     */
    private Date processSubscription(UserTicketsSubscription subscription){

        List<Train> actualTrains = getActualTrains(subscription);

        if(isTrainHasDeparted(actualTrains, subsctiption)){
            subscriptionService.deleteUserSubscription(subscription.getId());

            if(isTrainHasDeparted(actualTrains, subscription)){
                subscriptionService.deleteUserSubscription(subscription.getId());
                telgbotApplication.sendMessage(messagesService.getReplyMessage(subscription.getChatId(), "subcription.trainHasDeparted",
                        Emoji.NOTIFICATION_BELL, subscription.getTrainNumber(), subscription.getTrainName(),
                        subscription.getDateDepart(), subscription.getTimeDepart()));
            return;
            }

            actualTrains.forEach(actualTrains-> {

                if(actualTrains.getNumber().equals(subscription.getTrainNumber()) &&
                actualTrains.getDateDepart().equals(subscription.getDateDepart())){

                    List<Car> actualCarsWithMinimumPrice = carsProssingService.filterCarsWithMinimumPrice(actualTrains.getAvailableCars());

                    Map<String, List<Car>> updatedCarsNotification = processCarsLists(subscription.getSubscribedCars(),
                            actualCarsWithMinimumPrice);

                    if(!updatedCarsNotification.isEmpty()){
                        String priceChangesMessage = updatedCarsNotification.keySet().iterator().next();
                        List<Car> updatedCars = updatedCarsNotification.get(priceChangesMessage);

                        subscription.setSubscribedCars(updatedCars);
                        subscriptionService.saveUserSubscription(subscription);
                        sendUserNotification(subscription, priceChangesMessage, updatedCars);
                    }
                }
            });
        }

        private List<Train> getActualTrains(UserTicketsSubscription subscription){
            int stationDepartCode = stationCodeService.getStationCode(subscription.getStationDepart());
            int stationArrivalCode = stationCodeService.getStationCode(subscription.getStationArrival());
            Date dateDeparture = parseDateDeparture(subscription.getDateDepart());

            return trainTicketsGetInfoService.getTrainTicketsList(subscription.getChatId(),
                    stationDepartCode, stationArrivalCode, dateDeparture);
        }

        private boolean isTrainHasDeparted(List<Train> actualTrains, UserTicketsSubscription subscription){
            return actualTrains.stream().map(Train::getNumber).noneMatch(Predicate.isEqual(subscription.getTrainNumber()));
        }

        /**
         * Возвращает Мапу: Строку-уведомление и список обновленных цен в вагонах подписки.
         * Если цены не менялись, вернет пустую мапу.
         */

        private Map<String, List<Car>> processCarsLists(List<Car> subscripedCars, List<Car> actualCars){
            StringBuilder notificationMessage = new StringBuilder();

            for(Car subscribedCar: subscripedCars){

                for(Car actualCar: actualCars){
                    if(actualCar.getCatType().equals(subscribedCar.getCatType())){

                        if(actualCar.getMinimalPrice() > subscribedCar.getMinimalPrice()){
                            notificationMessage.append(messagesService.getReplyText("subscription.PriceUp", Emoji.NOTIFICATION_PRICE_UP,
                                    actualCar.getCatType(), subscribedCar.getMinimalPrice(), actualCar.getMinimalPrice()));
                            subscribedCar.setMinimalPrice(actualCar.getMinimalPrice());
                        } else if (actualCar.getMinimalPrice() < subscribedCar.getMinimalPrice()){
                            notificationMessage.append(messagesService.getReplyText("subscrition.PriceDown", Emoji.NOFIFICATION_PRICE_DOWN,
                                    actualCar.getCatType(), subscribedCar.getMinimalPrice(), actualCar.getMinimalPrice()));
                        }
                        subscribedCar.setFreeSeats(actualCar.getFreeSeats());
                    }
                }
            }
            return notificationMessage.length() == 0 ? Collections.emptyMap() : Collections.singletonMap(notificationMessage.toString(),
                    subscribedCars);
        }

        private void sendUserNotification(UserTicketsSubscription subscription, String priceChangeMessage, List<Car> updatedCars){
            StringBuilder notificationMessage = new StringBuilder(messagesService.getReplyText("subscrription.trainTicketsPriceChanges",
                    subscription.getDateDepart(), subscription.getTimeDepart(), subscription.getStationArrival())).append(priceChangeMessage);

            notificationMessage.append(messagesService.getReplyText("subscription.lastTicketPrices"));

            for(Car car: updatedCars){
                notificationMessage.append(messagesService.getReplyText("subscription.carsTicketsInfo", car.getCatType(),
                        car.getFreeSeats(), car.getMinimalPrice()));
            }

            telgbotApplication.sendMessage(subscription.getChatId(), notificationMessage.toString());
        }

        private Date parseDateDeparture(String dateDeparture){
            Date dateDepart = null;

            try {
                dateDepart = DATE_FORMAT.parse(dateDeparture);

            } catch (ParseException e){
                e.printStackTrace();
            }

            return dateDepart;

        }

  }

