library(ggplot2)
library(gridExtra)


args <- commandArgs(TRUE)

csvFile <- args[1]
pdfFile <- args[2]


dataFit <- read.table(file=csvFile, header=T, sep=",")


corMax <- cor(dataFit$prog_dist, dataFit$fit_max)
corAvg <- cor(dataFit$prog_dist, dataFit$fit_avg)

corMaxTest <- cor.test(dataFit$prog_dist, dataFit$fit_max)
corAvgTest <- cor.test(dataFit$prog_dist, dataFit$fit_avg)

maxText <- paste("correlation: ", round(corMax, digits=2))
maxText <- paste(maxText, "\np-value: ", round(corMaxTest$p.value, digits=5))

avgText <- paste("correlation: ", round(corAvg, digits=2))
avgText <- paste(avgText, "\np-value: ", round(corAvgTest$p.value, digits=5))

p1 <- ggplot(dataFit, aes(x=prog_dist, y=fit_max, label=prog)) + geom_point() + geom_text(aes(colour=group)) + geom_smooth(method="lm", fill=NA)
p1 <- p1 + theme(axis.line = element_line(colour = "black"),
    panel.grid.major = element_blank(),
    panel.grid.minor = element_blank(),
    panel.border = element_blank(),
    panel.background = element_blank()) 
p1 <- p1 + xlab("program distance")
p1 <- p1 + ylab("fitness (max)")
p1 <- p1 + geom_text(aes(x2, y2, label=texthere), 
          data.frame(x2=0.006, y2=0.8, texthere=maxText))
p1 <- p1 + opts(legend.position="none")


p2 <- ggplot(dataFit, aes(x=prog_dist, y=fit_avg, label=prog)) + geom_point() + geom_text(aes(colour=group)) + geom_smooth(method="lm", fill=NA)
p2 <- p2 + theme(axis.line = element_line(colour = "black"),
    panel.grid.major = element_blank(),
    panel.grid.minor = element_blank(),
    panel.border = element_blank(),
    panel.background = element_blank()) 
p2 <- p2 + xlab("program distance")
p2 <- p2 + ylab("fitness (mean)")
p2 <- p2 + geom_text(aes(x2, y2, label=texthere), 
          data.frame(x2=0.006, y2=0.6, texthere=avgText))
p2 <- p2 + opts(legend.position="none")


dataFit$group2 <- with(dataFit, relevel(group, "near"))

p3 <- ggplot(dataFit, aes(x=group2, y=fit_max)) + geom_boxplot(aes(colour=group))
p3 <- p3 + theme(axis.line = element_line(colour = "black"),
    panel.grid.major = element_blank(),
    panel.grid.minor = element_blank(),
    panel.border = element_blank(),
    panel.background = element_blank()) 
p3 <- p3 + xlab("group")
p3 <- p3 + ylab("fitness (max)")
p3 <- p3 + opts(legend.position="none")


p4 <- ggplot(dataFit, aes(x=group2, y=fit_avg)) + geom_boxplot(aes(colour=group))
p4 <- p4 + theme(axis.line = element_line(colour = "black"),
    panel.grid.major = element_blank(),
    panel.grid.minor = element_blank(),
    panel.border = element_blank(),
    panel.background = element_blank()) 
p4 <- p4 + xlab("group")
p4 <- p4 + ylab("fitness (mean)")
p4 <- p4 + opts(legend.position="none")


plots = arrangeGrob(p1, p2, p3, p4)
pdf(file=pdfFile, height=8, width=10)
grid.arrange(plots, heights=c(1), nrow=1)