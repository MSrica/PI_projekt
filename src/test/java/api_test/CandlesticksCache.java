package api_test;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Illustrates how to use the klines/candlesticks event stream to create a local cache of bids/asks for a symbol.
 */
public class CandlesticksCache {

  /**
   * Key is the start/open time of the candle, and the value contains candlestick date.
   */
  public Map<Long, Candlestick> candlesticksCache;

  public CandlesticksCache(String symbol, CandlestickInterval interval) {
    initializeCandlestickCache(symbol, interval);
    startCandlestickEventStreaming(symbol, interval);
  }

  /**
   * Initializes the candlestick cache by using the REST API.
   */
  private void initializeCandlestickCache(String symbol, CandlestickInterval interval) {
    BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
    BinanceApiRestClient client = factory.newRestClient();
    List<Candlestick> candlestickBars = client.getCandlestickBars(symbol.toUpperCase(), interval);

    this.candlesticksCache = new TreeMap<>();
    for (Candlestick candlestickBar : candlestickBars) {
      candlesticksCache.put(candlestickBar.getOpenTime(), candlestickBar);
    }
  }

  public static Candlestick updateCandlestick;

  public static Candlestick getUpdateCandlestick() {
    return updateCandlestick;
  }

  /**
   * Begins streaming of depth events.
   */
  private BinanceApiClientFactory factory;
  private BinanceApiWebSocketClient client;

  public BinanceApiWebSocketClient getClient() {
    return client;
  }

  private void startCandlestickEventStreaming(String symbol, CandlestickInterval interval) {
    factory = BinanceApiClientFactory.newInstance();
    client = factory.newWebSocketClient();  //TODO: close client

    client.onCandlestickEvent(symbol.toLowerCase(), interval, response -> {
      Long openTime = response.getOpenTime();
      updateCandlestick = candlesticksCache.get(openTime);
      if (updateCandlestick == null) {
        // new candlestick
        updateCandlestick = new Candlestick();
      }
      // update candlestick with the stream data
      updateCandlestick.setOpenTime(response.getOpenTime());
      updateCandlestick.setOpen(response.getOpen());
      updateCandlestick.setLow(response.getLow());
      updateCandlestick.setHigh(response.getHigh());
      updateCandlestick.setClose(response.getClose());
      updateCandlestick.setCloseTime(response.getCloseTime());
      updateCandlestick.setVolume(response.getVolume());
      updateCandlestick.setNumberOfTrades(response.getNumberOfTrades());
      updateCandlestick.setQuoteAssetVolume(response.getQuoteAssetVolume());
      updateCandlestick.setTakerBuyQuoteAssetVolume(response.getTakerBuyQuoteAssetVolume());
      updateCandlestick.setTakerBuyBaseAssetVolume(response.getTakerBuyQuoteAssetVolume());

      // Store the updated candlestick in the cache
      candlesticksCache.put(openTime, updateCandlestick);
      System.out.println(updateCandlestick);

    });

    client.onCandlestickEvent(symbol.toLowerCase(), interval, response -> {
      Long openTime = response.getOpenTime();
      updateCandlestick = candlesticksCache.get(openTime);
      if (updateCandlestick == null) {
        // new candlestick
        updateCandlestick = new Candlestick();
      }
      // update candlestick with the stream data
      updateCandlestick.setOpenTime(response.getOpenTime());
      updateCandlestick.setOpen(response.getOpen());
      updateCandlestick.setLow(response.getLow());
      updateCandlestick.setHigh(response.getHigh());
      updateCandlestick.setClose(response.getClose());
      updateCandlestick.setCloseTime(response.getCloseTime());
      updateCandlestick.setVolume(response.getVolume());
      updateCandlestick.setNumberOfTrades(response.getNumberOfTrades());
      updateCandlestick.setQuoteAssetVolume(response.getQuoteAssetVolume());
      updateCandlestick.setTakerBuyQuoteAssetVolume(response.getTakerBuyQuoteAssetVolume());
      updateCandlestick.setTakerBuyBaseAssetVolume(response.getTakerBuyQuoteAssetVolume());

      // Store the updated candlestick in the cache
      candlesticksCache.put(openTime, updateCandlestick);
      System.out.println(updateCandlestick);

    });

    System.out.println("gCC: " + getCandlesticksCache());
  }

  /**
   * @return a klines/candlestick cache, containing the open/start time of the candlestick as the key,
   * and the candlestick data as the value.
   */
  public Map<Long, Candlestick> getCandlesticksCache() {
    return candlesticksCache;
  }

  //public static void main(String[] args) {
  //  new CandlesticksCache("ETHBTC", CandlestickInterval.ONE_MINUTE);
  //}
}