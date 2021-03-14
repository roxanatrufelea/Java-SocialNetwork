package socialnetwork.utils.events;

import socialnetwork.domain.User;

public class RequestChangeEvent {

    private ChangeEventType type;
    private User data,oldData;

    public RequestChangeEvent(ChangeEventType type, User data) {
        this.type = type;
        this.data = data;
    }

    public RequestChangeEvent(ChangeEventType type, User data, User oldData) {
        this.type = type;
        this.data = data;
        this.oldData = oldData;
    }

    public ChangeEventType getType() {
        return type;
    }

    public User getData() {
        return data;
    }

    public User getOldData() {
        return oldData;
    }
}
