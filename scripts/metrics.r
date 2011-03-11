library(ggplot2)

pdf(file="metrics.pdf")

metrics <- read.csv("evc.csv")

p <- qplot(evc_in, evc_out, data=metrics)
print(p)
