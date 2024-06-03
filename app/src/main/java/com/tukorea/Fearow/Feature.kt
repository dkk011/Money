package com.tukorea.Fearow
import com.google.gson.annotations.SerializedName

data class FeatureCollection(
    @SerializedName("type") val type: String,
    @SerializedName("features") val features: List<Feature>
)

data class Feature(
    @SerializedName("type") val type: String,
    @SerializedName("properties") val properties: Properties,
    @SerializedName("geometry") val geometry: Geometry
)

data class Properties(
    @SerializedName("adm_nm") val admNm: String,
    @SerializedName("adm_cd") val admCd: String,
    @SerializedName("adm_cd2") val admCd2: String,
    @SerializedName("sgg") val sgg: String,
    @SerializedName("sido") val sido: String,
    @SerializedName("sidonm") val sidonm: String,
    @SerializedName("temp") val temp: String,
    @SerializedName("sggnm") val sggnm: String,
    @SerializedName("adm_cd8") val admCd8: String
)

data class Geometry(
    @SerializedName("type") val type: String,
    @SerializedName("coordinates") val coordinates: List<List<List<List<Double>>>>
)
