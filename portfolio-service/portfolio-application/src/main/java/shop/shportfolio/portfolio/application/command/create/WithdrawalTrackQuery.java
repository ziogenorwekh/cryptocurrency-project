package shop.shportfolio.portfolio.application.command.create;

public class WithdrawalTrackQuery {
    private String transactionId;
    private String userId;

    public WithdrawalTrackQuery(String transactionId, String userId) {
        this.transactionId = transactionId;
        this.userId = userId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
