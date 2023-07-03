package liteweb.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CustomizedLock implements Lock {

    /** Synchronizer providing all implementation mechanics */
    private final Sync sync;

    public CustomizedLock() {
        sync = new NonfairSync();
    }

    @Override
    public void lock() {
        sync.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return sync.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryLockNanos(unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    /**
     * Base of synchronization control for this lock. Subclassed
     * into fair and nonfair versions below. Uses AQS state to
     * represent the number of holds on the lock.
     */
    abstract static class Sync extends AbstractQueuedSynchronizer {

        /**
         * Performs non-fair tryLock.
         */
        final boolean tryLock() {
            Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (compareAndSetState(0, 1)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            } else if (getExclusiveOwnerThread() == current) {
                if (++c < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(c);
                return true;
            }
            return false;
        }

        /**
         * Checks for reentrancy and acquires if lock immediately
         * available under fair vs nonfair rules. Locking methods
         * perform initialTryLock check before relaying to
         * corresponding AQS acquire methods.
         */
        abstract boolean initialTryLock();

        final void lock() {
            if (!initialTryLock())
                acquire(1);
        }

        final void lockInterruptibly() throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            if (!initialTryLock())
                acquireInterruptibly(1);
        }

        final boolean tryLockNanos(long nanos) throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            return initialTryLock() || tryAcquireNanos(1, nanos);
        }

        @Override
        protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (getExclusiveOwnerThread() != Thread.currentThread())
                throw new IllegalMonitorStateException();
            boolean free = (c == 0);
            if (free)
                setExclusiveOwnerThread(null);
            setState(c);
            return free;
        }

        @Override
        protected final boolean isHeldExclusively() {
            // While we must in general read state before owner,
            // we don't need to do so to check if current thread is owner
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }
    }

    /**
     * Sync object for non-fair locks
     */
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        boolean initialTryLock() {
            Thread current = Thread.currentThread();
            if (compareAndSetState(0, 1)) { // first attempt is unguarded
                setExclusiveOwnerThread(current);
                return true;
            } else if (getExclusiveOwnerThread() == current) {
                int c = getState() + 1;
                if (c < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(c);
                return true;
            } else
                return false;
        }

        /**
         * Acquire for non-reentrant cases after initialTryLock prescreen
         */
        @Override
        protected boolean tryAcquire(int acquires) {
            if (getState() == 0 && compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }
    }

}
