package com.trivago.mp.casestudy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataUpload {

	private Map<Integer, List<HotelHelper>> resultMapHotelFile;
	private Map<Integer, List<Advertiser>> resultMapAdvertiserByHotel;
	private static List<Hotel> hotelList;

	public DataUpload() {
		this.resultMapHotelFile = new HashMap<>();
		this.resultMapAdvertiserByHotel = new HashMap<>();
		this.hotelList = new ArrayList<>();

	}

	public Map<Integer, List<Advertiser>> getResultMapAdvertiserByHotel() {
		return resultMapAdvertiserByHotel;
	}

	private static Hotel getHotel(HotelXAdvertiser h) {
		return hotelList.stream().filter(t -> t.getId() == h.getHotelId()).findFirst().get();

	}

	public Map<Integer, Advertiser> processAdvertisersFile(String FilePath) {

		List<Advertiser> list = new ArrayList<Advertiser>();

		Map<Integer, Advertiser> map = new HashMap<>();

		try {

			File file = new File(FilePath);

			InputStream fileStream = new FileInputStream(file);

			BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));

			list = br.lines().skip(1).map(mapToAdvertisers).collect(Collectors.toList());

			list.stream().forEach(item -> map.put(item.getId(), item));

			br.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		return map;
	}

	public static Function<String, Advertiser> mapToAdvertisers = (line) -> {
		String[] p = line.split(",");
		return new Advertiser(Integer.parseInt(p[0]), p[1]);
	};

	public Map<String, Integer> processCitiesFile(String FilePath) {

		Map<String, Integer> map = new HashMap<>();

		try {

			File file = new File(FilePath);

			InputStream fileStream = new FileInputStream(file);

			BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));

			map = br.lines().skip(1).map(mapToCities).collect(Collectors.toMap(City::getName, City::getId));

			br.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		return map;
	}

	public static Function<String, City> mapToCities = (line) -> {

		String[] p = line.split(",");

		return new City(Integer.parseInt(p[0]), p[1]);
	};

	public Map<Integer, List<HotelHelper>> processHotelsFile(String FilePath) {

		List<HotelHelper> hotelHelperList = new ArrayList<>();

		try {

			File file = new File(FilePath);

			InputStream fileStream = new FileInputStream(file);

			BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));

			hotelHelperList = br.lines().skip(1).map(mapToHotel).collect(Collectors.toList());

			resultMapHotelFile = hotelHelperList.stream().collect(Collectors.groupingBy(HotelHelper::getCityId));

			hotelList = hotelHelperList.stream().map(HotelHelper::getHotel).collect(Collectors.toList());

			br.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		return resultMapHotelFile;
	}

	public List<Hotel> getHotelList() {
		return hotelList;
	}

	public static Function<String, HotelHelper> mapToHotel = (line) -> {

		String[] p = line.split(",");

		Hotel hotel = new Hotel(Integer.parseInt(p[0]), p[4], Integer.parseInt(p[5]), Integer.parseInt(p[6]));

		HotelHelper hotelHelper = new HotelHelper(hotel, Integer.parseInt(p[1]), Integer.parseInt(p[2]),
				Integer.parseInt(p[3]));

		return hotelHelper;
	};

	public Map<Integer, List<Hotel>> processHotelAdvertiserFile(String FilePath,
			Map<Integer, Advertiser> mapAdvertiser) {

		Map<Integer, List<HotelXAdvertiser>> dataGroup = new HashMap<>();
		Map<Integer, List<Hotel>> resultMapHotel = new HashMap<>();
		List<HotelXAdvertiser> hotelXAdvertiserList = new ArrayList<>();

		try {

			File file = new File(FilePath);

			InputStream fileStream = new FileInputStream(file);

			BufferedReader br = new BufferedReader(new InputStreamReader(fileStream));

			hotelXAdvertiserList = br.lines().skip(1).map(mapToHotelToAdvertiser).collect(Collectors.toList());

			dataGroup = hotelXAdvertiserList.stream().collect(Collectors.groupingBy(HotelXAdvertiser::getHotelId));

			dataGroup.entrySet().stream().forEach(d -> resultMapAdvertiserByHotel.put(d.getKey(),
					d.getValue().stream().map(v -> mapAdvertiser.get(v.getAdvertiser())).collect(Collectors.toList())));

			dataGroup = hotelXAdvertiserList.stream().collect(Collectors.groupingBy(HotelXAdvertiser::getAdvertiser));

			resultMapHotel = dataGroup.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
					e -> e.getValue().stream().map(DataUpload::getHotel).collect(Collectors.toList())));

			br.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		return resultMapHotel;

	}

	public static Function<String, HotelXAdvertiser> mapToHotelToAdvertiser = (line) -> {

		String[] p = line.split(",");

		return new HotelXAdvertiser(Integer.parseInt(p[1]), Integer.parseInt(p[0]));
	};

	private static class HotelXAdvertiser {

		private Integer hotel;
		private Integer advertiser;

		public HotelXAdvertiser(Integer hotelId, Integer advertiser) {

			this.hotel = hotelId;
			this.advertiser = advertiser;
		}

		public Integer getHotelId() {
			return hotel;
		}

		public void setHotelId(Integer hotelId) {
			this.hotel = hotelId;
		}

		public Integer getAdvertiser() {
			return advertiser;
		}

		public void setAdvertiserId(Integer advertiser) {
			this.advertiser = advertiser;
		}

	}

}
