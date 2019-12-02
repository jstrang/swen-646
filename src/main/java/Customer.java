public class Customer {

    private String firstName;
    private String lastName;
    private Address mailingAddress;
    private String phoneNumber;
    private String email;

    public Customer(String firstName, String lastName, Address address, String phoneNumber, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mailingAddress = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("First Name: ").append(firstName).append("\n")
                .append("Last Name: ").append(lastName).append("\n")
                .append("Mailing Address: ").append(mailingAddress.toString()).append("\n")
                .append("Phone Number: ").append(phoneNumber).append("\n")
                .append("Email: ").append(email);
        return builder.toString();
    }
}
