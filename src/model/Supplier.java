package model;

public class Supplier {
    private int supplierId;
    private String name;
    private String contactPerson;
    private String address;
    private String phone;
    private String email;
    private String paymentTerms;

    public Supplier() {}

    public Supplier(int supplierId, String name, String contactPerson, String address, String phone, String email, String paymentTerms) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactPerson = contactPerson;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.paymentTerms = paymentTerms;
    }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }
}
