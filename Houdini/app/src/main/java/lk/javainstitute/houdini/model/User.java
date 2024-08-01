package lk.javainstitute.houdini.model;

public class User {
    private String userName;
    private String userEmail;
    private String userAddress;
    private String userContactNo;
    private String userImage;

    public User() {
    }

    public User(String userName, String userEmail, String userAddress, String userContactNo, String userImage) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userAddress = userAddress;
        this.userContactNo = userContactNo;
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserContactNo() {
        return userContactNo;
    }

    public void setUserContactNo(String userContactNo) {
        this.userContactNo = userContactNo;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
