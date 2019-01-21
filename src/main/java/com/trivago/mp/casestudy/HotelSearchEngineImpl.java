package com.trivago.mp.casestudy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * TODO: Implement this class. Your task will be to implement two functions, one
 * for loading the data which is stored as .csv files in the ./data folder and
 * one for performing the actual search.
 */
public class HotelSearchEngineImpl implements HotelSearchEngine {

	Map<String, Integer> mapCityidByCityName;
	Map<Integer, List<HotelHelper>> mapHotelsByCities;
	Map<Integer, Advertiser> mapAdvertiser;
	Map<Integer, List<Hotel>> mapAdvertiserByHotel;
	DataUpload dataUpload;

	@Override
	public void initialize() {
		dataUpload = new DataUpload();
		mapCityidByCityName = dataUpload.processCitiesFile("./data/cities.csv");
		mapHotelsByCities = dataUpload.processHotelsFile("./data/hotels.csv");
		mapAdvertiser = dataUpload.processAdvertisersFile("./data/advertisers.csv");
		mapAdvertiserByHotel = dataUpload.processHotelAdvertiserFile("./data/hotel_advertiser.csv", mapAdvertiser);
	}

	@Override
	public List<HotelWithOffers> performSearch(String cityName, DateRange dateRange, OfferProvider offerProvider) {

		List<SearchHelper> forSearch = new ArrayList<>();

		List<Hotel> hotelListByCity = mapHotelsByCities.get(mapCityidByCityName.get(cityName)).stream()
				.map(HotelHelper::getHotel).collect(Collectors.toList());

		List<Advertiser> advertiserList = hotelListByCity.stream()
				.flatMap(h -> dataUpload.getResultMapAdvertiserByHotel().get(h.getId()).stream())
				.collect(Collectors.toList());

		advertiserList.stream().forEach(a -> {
			SearchHelper sh = new SearchHelper();
			mapAdvertiserByHotel.get(a.getId()).forEach(i -> {
				if (hotelListByCity.contains(i)) {
					sh.addHotelIds(i.getId());
				}
			});
			sh.setAdvertiser(a);
			forSearch.add(sh);
		});

		List<Entry<Integer, Offer>> search = forSearch
				.stream().flatMap(s -> offerProvider
						.getOffersFromAdvertiser(s.getAdvertiser(), s.getHotelIds(), dateRange).entrySet().stream())
				.collect(Collectors.toList());

		Map<Integer, List<Offer>> map = search.stream().collect(
				Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

		return map.entrySet().stream().map(m -> {
			HotelWithOffers hwo = new HotelWithOffers(
					dataUpload.getHotelList().stream().filter(h -> h.getId() == m.getKey()).findFirst().get());
			hwo.setOffers(m.getValue());
			return hwo;
		}).collect(Collectors.toList());

	}

	private static class SearchHelper {

		private Advertiser advertiser;
		private List<Integer> hotelIds;

		public SearchHelper() {
			this.hotelIds = new ArrayList<>();
		}

		public Advertiser getAdvertiser() {
			return advertiser;
		}

		public void setAdvertiser(Advertiser advertiser) {
			this.advertiser = advertiser;
		}

		public List<Integer> getHotelIds() {
			return hotelIds;
		}

		public void setHotelIds(List<Integer> hotelIds) {
			this.hotelIds = hotelIds;
		}

		public void addHotelIds(Integer e) {
			hotelIds.add(e);
		}

	}
}
