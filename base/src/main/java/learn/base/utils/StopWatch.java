package learn.base.utils;


import java.util.concurrent.TimeUnit;

/**
 * 秒表 （计时器）
 *
 * @author Zephyr
 * @date 2021/1/22.
 */
public class StopWatch {

    private String taskName;
    private StopWatch.TaskState state = TaskState.UNSTARTED;


    /** Start time of the current task. */
    private long startTimeNanos;
    /** End time of the current task. */
    private long stopTimeNanos;


    public StopWatch() {
        this("");
    }

    public StopWatch(String taskName) {
        this.taskName = taskName;
    }

    public static StopWatch createAndStart(String taskName) {
        StopWatch stopWatch = new StopWatch(taskName);
        stopWatch.start();
        return stopWatch;
    }


    public void start() {
        if (this.state == TaskState.STOPPED) {
            throw new IllegalStateException("Stopwatch must be reset before being restarted. ");
        } else if (this.state != TaskState.UNSTARTED) {
            throw new IllegalStateException("Stopwatch already started. ");
        } else {
            this.startTimeNanos = System.nanoTime();
            this.state = TaskState.RUNNING;
        }
    }

    /**
     * 暂停计时
     */
    public void suspend() {
        if (this.state != TaskState.RUNNING) {
            throw new IllegalStateException("Stopwatch must be running to suspend. ");
        } else {
            this.stopTimeNanos = System.nanoTime();
            this.state = TaskState.SUSPENDED;
        }
    }

    /**
     * 继续计时
     */
    public void resume() {
        if (this.state != TaskState.SUSPENDED) {
            throw new IllegalStateException("Stopwatch must be suspended to resume. ");
        } else {
            this.startTimeNanos += System.nanoTime() - this.stopTimeNanos;
            this.state = TaskState.RUNNING;
        }
    }

    public void stop() {
        if (this.state != TaskState.RUNNING && this.state != TaskState.SUSPENDED) {
            throw new IllegalStateException("Can't stop StopWatch: it's not running");
        }
        this.stopTimeNanos = System.nanoTime();
        this.state = TaskState.STOPPED;
    }

    public String stopAndPrint() {
        this.stop();
        return this.prettyPrint();
    }

    public String prettyPrint() {
        long timeMillisThreshold = 10_000L;
        if (this.state.isStopped()) {
            long costNanoTimes = this.stopTimeNanos - this.startTimeNanos;
            long costMillis = TimeUnit.NANOSECONDS.toMillis(costNanoTimes);
            if (costMillis < timeMillisThreshold) {
                return String.format("=== StopWatch : %s cost %d ns , means %d ms ===",
                        this.taskName, costNanoTimes, costMillis);
            } else {
                return String.format("=== StopWatch : %s cost %d ms , means %d s ===",
                        this.taskName, costMillis, TimeUnit.NANOSECONDS.toSeconds(costNanoTimes));
            }
        } else {
            long costMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTimeNanos);
            if (costMillis < timeMillisThreshold) {
                return "=== StopWatch is still running, it has been started for " + costMillis + " ms ===";
            } else {
                return String.format("=== StopWatch is still running, it has been started for %d ms , means %d s ===", costMillis, costMillis / 1000);
            }
        }
    }

    public boolean isRunning() {
        return this.state == TaskState.RUNNING;
    }

    public long getTime() {
        return this.getNanoTime() / 1000000L;
    }

    public long getTime(TimeUnit timeUnit) {
        return timeUnit.convert(this.getNanoTime(), TimeUnit.NANOSECONDS);
    }

    public long getNanoTime() {
        if (this.state != TaskState.STOPPED && this.state != TaskState.SUSPENDED) {
            if (this.state == TaskState.UNSTARTED) {
                return 0L;
            } else if (this.state == TaskState.RUNNING) {
                return System.nanoTime() - this.startTimeNanos;
            } else {
                throw new RuntimeException("Illegal running state has occurred.");
            }
        } else {
            return this.stopTimeNanos - this.startTimeNanos;
        }
    }


    /**
     * 计时器状态枚举
     */
    private enum TaskState {
        UNSTARTED {
            boolean isStarted() {
                return false;
            }
            boolean isStopped() {
                return true;
            }
            boolean isSuspended() {
                return false;
            }
        },
        RUNNING {
            boolean isStarted() {
                return true;
            }
            boolean isStopped() {
                return false;
            }
            boolean isSuspended() {
                return false;
            }
        },
        STOPPED {
            boolean isStarted() {
                return false;
            }
            boolean isStopped() {
                return true;
            }
            boolean isSuspended() {
                return false;
            }
        },
        SUSPENDED {
            boolean isStarted() {
                return true;
            }
            boolean isStopped() {
                return false;
            }
            boolean isSuspended() {
                return true;
            }
        };

        private TaskState() {
        }

        abstract boolean isStarted();
        abstract boolean isStopped();
        abstract boolean isSuspended();
    }
}
