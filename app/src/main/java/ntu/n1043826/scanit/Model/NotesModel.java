package ntu.n1043826.scanit.Model;

public class NotesModel {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String id;
    public String title;
    public String desc;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String category;
    public String date;

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String imgurl;
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public NotesModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public NotesModel(String id, String title, String desc, String category, String date, String imgurl) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.category = category;
        this.date = date;
        this.imgurl = imgurl;
    }



}