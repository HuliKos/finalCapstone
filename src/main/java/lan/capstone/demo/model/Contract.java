package lan.capstone.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contracts")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    @Column(name = "amount", nullable = false, length = 255)
    private String amount;
    @Column(name = "start_date", nullable = false, length = 255)
    private String startDate;
    @Column(name = "end_date", nullable = false, length = 255)
    private String endDate;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Contract() {}

    public Contract(String title, String amount, String start_date, String end_date, User user) {
        this.title = title;
        this.amount = amount;
        this.startDate = start_date;
        this.endDate = end_date;
        this.user = user;
    }

    public long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String start_date) {
        this.startDate = start_date;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String end_date) {
        this.endDate = end_date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", amount='" + amount + '\'' +
                ", start_date='" + startDate + '\'' +
                ", end_date='" + endDate + '\'' +
                ", user_id='" + user.getId() + '\'' +
                '}';
    }
}
