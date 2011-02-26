data <- read.table("LocalityNoGeography_2011_02_24.R", header=T)
summary(data)

data$SUCCESS_BIN <- ifelse(data$SUCCESS=='true', 1, 0)
# plot SUCCESS_BIN ~ GEOGRPHY_TYPE limiting ourselves to the problem where the success rate is between 0 and 1.
hist(SUCCESS_BIN ~ CARRYING_CAPACITY, data=data)

successfulRuns <- subset(data, data$SUCCESS == "true")

partTree = rpart(COMPLETED_EVALS ~ PROMOTE_SMALL_POPULATIONS+TOROIDAL+WORLD_DIMENSIONS+REPRODUCTION_RATE+CARRYING_CAPACITY+ELITE_PROPORTION+STARTING_POPULATION, data=successfulRuns, cp=0.005)
plot(partTree)
text(partTree, use.n=TRUE, pretty=0)