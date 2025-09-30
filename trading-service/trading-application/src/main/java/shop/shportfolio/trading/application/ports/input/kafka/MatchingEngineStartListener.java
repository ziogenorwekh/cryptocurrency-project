package shop.shportfolio.trading.application.ports.input.kafka;

public interface MatchingEngineStartListener {

    void sendOpenOrdersToMatchingEngine();
}
