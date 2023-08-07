
public interface NotificationSubject {
    void registerObserver(NotificationObserver observer);
    void removeObserver(NotificationObserver observer);
    void notifyObservers(String username, String tweet);
}