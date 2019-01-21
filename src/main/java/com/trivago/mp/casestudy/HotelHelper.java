package com.trivago.mp.casestudy;

public class HotelHelper {

	private Hotel hotel;
	private Integer cityId;
	private Integer clicks;
	private Integer impressions;

	public HotelHelper(Hotel hotel, Integer cityId, Integer clicks, Integer impressions) {
		super();
		this.hotel = hotel;
		this.cityId = cityId;
		this.clicks = clicks;
		this.impressions = impressions;
	}

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Integer getClicks() {
		return clicks;
	}

	public void setClicks(Integer clicks) {
		this.clicks = clicks;
	}

	public Integer getImpressions() {
		return impressions;
	}

	public void setImpressions(Integer impressions) {
		this.impressions = impressions;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

}
