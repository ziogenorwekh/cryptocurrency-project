package shop.shportfolio.user.application.command.track;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.user.application.command.vo.TransactionHistoryVO;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TrackUserTrHistoryQueryResponse {


    private List<TransactionHistoryVO> transactionHistoryList;

    @Builder
    public TrackUserTrHistoryQueryResponse() {
        this.transactionHistoryList = new ArrayList<>();
    }
}
