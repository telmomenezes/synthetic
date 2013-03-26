library(ggplot2)


args <- commandArgs(TRUE)

csvFile <- args[1]
pdfFile <- args[2]


pdf(file=pdfFile, height=5, width=5)

dataFit<-read.table(file=csvFile, header=T, sep=",")

p <- ggplot(dataFit, aes(x=prog, y=fit)) + geom_boxplot()
p <- p + theme(axis.line = element_line(colour = "black"),
    panel.grid.major = element_blank(),
    panel.grid.minor = element_blank(),
    panel.border = element_blank(),
    panel.background = element_blank()) 
p <- p + xlab("program")
p <- p + ylab("fitness")
p