package observer;

import lombok.Data;
import lombok.Getter;
import observer.enums.NotificationCode;
@Data
@Getter
public class Notification {
    private final NotificationCode notificationCode;
    private Object data;

     public Notification(NotificationCode notificationCode,Object data){
         this.notificationCode = notificationCode;
         this.data= data;
     }


}
