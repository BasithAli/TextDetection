package ntu.n1043826.scanit.Model;

public class QRModel {
    public String id;
    public String type;
    public String text;
    public String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public QRModel() {
    }

    public QRModel(String id, String type, String text, String date) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.date = date;
    }
}
