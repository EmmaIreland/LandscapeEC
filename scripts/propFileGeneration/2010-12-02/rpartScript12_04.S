data <- read.table("all_output.R", header=T)
summary(data)

data$SUCCESS_BIN <- ifelse(data$SUCCESS=='true', 1, 0)
# plot SUCCESS_BIN ~ GEOGRPHY_TYPE limiting ourselves to the problem where the success rate is between 0 and 1.
boxplot(BEST_FITNESS ~ GEOGRAPHY_TYPE, data=data)
