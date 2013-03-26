args <- commandArgs(TRUE)

csvFile <- args[1]
pdfFile <- args[2]


pdf(file=pdfFile, height=5, width=5)

distm<-read.table(file=csvFile, header=T, sep=",")

d <- as.dist(distm)
c <- hclust(d, "ward")
plot(c)
