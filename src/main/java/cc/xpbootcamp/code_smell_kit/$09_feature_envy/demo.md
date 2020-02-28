一个类大量调用另一个类的函数

在这里，MachineShopSimulator类中的simulate方法中调用moveToNextMachine。
moveToNextMachine位于MachineShopSimulator类中，但不在Job类中。
但是，该方法所需的绝大多数信息都是从Job类中提取的。

```java
public class MachineShopSimulator {
static boolean moveToNextMachine(Job theJob, SimulationResults simulationResults) {
        if (theJob.getTaskQ().isEmpty()) {// no next task
            simulationResults.setJobCompletionData(theJob.getId(), timeNow, timeNow - theJob.getLength());
            return false;
        } else {// theJob has a next task
                // get machine for next task
            int p = ((Task) theJob.getTaskQ().getFrontElement()).getMachine();
            // put on machine p's wait queue
            machine[p].getJobQ().put(theJob);
            theJob.setArrivalTime(timeNow);
            // if p idle, schedule immediately
            if (eList.nextEventTime(p) == largeTime) {// machine is idle
                changeState(p);
            }
            return true;
        }
    }
 static void simulate(SimulationResults simulationResults) {
        while (numJobs > 0) {// at least one job left
            int nextToFinish = eList.nextEventMachine();
            timeNow = eList.nextEventTime(nextToFinish);
            // change job on machine nextToFinish
            Job theJob = changeState(nextToFinish);
            // move theJob to its next machine
            // decrement numJobs if theJob has finished
            if (theJob != null && !moveToNextMachine(theJob, simulationResults))
                numJobs--;
        }
    };
}
```

一个更简单的例子
Laboratory类中的calculateLevel方法几乎全部调用Image类中的方法
```java
public class Image {
    private String name;
    private Integer size;
    private Double minDuration;
    ...getter setter;
}
```

```java
public class Laboratory {
    private Image image;
    public String calculateLevel() {
        if(image.getName().equals("BIG DATA") || image.getName().equals("AI") || image.getMinDuration() >= 100.0) {
            return "Difficult";
        } else if (image.getMinDuration() >= 60.0) {
            return "Normal";
        }
        return "easy";
    }
}
```