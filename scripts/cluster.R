args <- commandArgs(TRUE)

csvFile <- args[1]
pdfFile <- args[2]


distm<-read.table(file=csvFile, header=T, sep=",")

d <- as.dist(distm)
c <- hclust(d, method="single")

pdf(file=pdfFile, height=5, width=5)
par(mar=c(0, 4, 4, 2))
plot(c, xlab="", sub="", main="")