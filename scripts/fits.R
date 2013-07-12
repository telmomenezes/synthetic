library(ggplot2)
library(gridExtra)


args <- commandArgs(TRUE)

csvFile <- args[1]
pdfFile <- args[2]


dataFit<-read.table(file=csvFile, header=T, sep=",")

p1 <- ggplot(dataFit, aes(x=prog, y=fit_max)) + geom_boxplot()
p1 <- p1 + theme(axis.line = element_line(colour = "black"),
    panel.grid.major = element_blank(),
    panel.grid.minor = element_blank(),
    panel.border = element_blank(),
    panel.background = element_blank()) 
p1 <- p1 + xlab("program")
p1 <- p1 + ylab("fitness (max)")

p2 <- ggplot(dataFit, aes(x=prog, y=fit_mean)) + geom_boxplot()
p2 <- p2 + theme(axis.line = element_line(colour = "black"),
    panel.grid.major = element_blank(),
    panel.grid.minor = element_blank(),
    panel.border = element_blank(),
    panel.background = element_blank()) 
p2 <- p2 + xlab("program")
p2 <- p2 + ylab("fitness (mean)")

plots = arrangeGrob(p1, p2)
pdf(file=pdfFile, height=10, width=10)
grid.arrange(plots, heights=c(1), nrow=1)
