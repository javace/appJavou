package br.com.javace.javou.model.participant;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rudsonlive on 10/07/15.
 */
public class Participant implements Parcelable {

    private int id;
    private String name;
    private String phone;
    private String email;
    private String photo;
    private int shirtSize;
    private boolean group;
    private boolean attend;
    private String nameEvent;
    private String birthDate;
    private boolean sex;
    private String company;
    private boolean raffled;
    private int code;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(String.valueOf(this.getId()));
        dest.writeString(this.getName());
        dest.writeString(this.getPhone());
        dest.writeString(this.getEmail());
        dest.writeString(this.getPhoto());
        dest.writeInt(this.getShirtSize());
        dest.writeByte(this.isGroup() ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAttend() ? (byte) 1 : (byte) 0);

        dest.writeString(this.getNameEvent());

        dest.writeString(this.getBirthDate());
        dest.writeByte(this.isSex() ? (byte) 1 : (byte) 0);
        dest.writeString(this.getCompany());

        dest.writeByte(this.isRaffled() ? (byte) 1 : (byte) 0);
        dest.writeInt(this.getCode());
    }

    public Participant() {
    }

    protected Participant(Parcel in) {
        this.setId(in.readInt());
        this.setName(in.readString());
        this.setPhone(in.readString());
        this.setEmail(in.readString());
        this.setPhoto(in.readString());
        this.setShirtSize(in.readInt());
        this.setGroup(in.readByte() != 0);
        this.setAttend(in.readByte() != 0);

        this.setNameEvent(in.readString());

        this.setBirthDate(in.readString());
        this.setSex(in.readByte() != 0);
        this.setCompany(in.readString());

        this.setRaffled(in.readByte() != 0);
        this.setCode(in.readInt());
    }

    public static final Parcelable.Creator<Participant> CREATOR = new Parcelable.Creator<Participant>() {
        public Participant createFromParcel(Parcel source) {
            return new Participant(source);
        }

        public Participant[] newArray(int size) {
            return new Participant[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getShirtSize() {
        return shirtSize;
    }

    public void setShirtSize(int shirtSize) {
        this.shirtSize = shirtSize;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public boolean isAttend() {
        return attend;
    }

    public void setAttend(boolean attend) {
        this.attend = attend;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNameEvent() {
        return nameEvent;
    }

    public void setNameEvent(String nameEvent) {
        this.nameEvent = nameEvent;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRaffled() {
        return raffled;
    }

    public void setRaffled(boolean raffled) {
        this.raffled = raffled;
    }

    public boolean getSex() {
        return sex;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
