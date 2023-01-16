package lych.soulcraft.extension.soulpower.control;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.task.Task;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class BrainTaskHelper implements Predicate<Task<?>> {
    private final Task<? super MobEntity> task;
    private final Predicate<? super Task<?>> taskTester;

    private BrainTaskHelper(Task<? super MobEntity> task, Predicate<? super Task<?>> taskTester) {
        this.task = task;
        this.taskTester = taskTester;
    }

    public static BrainTaskHelper of(Task<? super MobEntity> task) {
        return of(task, t -> false);
    }

    public static BrainTaskHelper of(Task<? super MobEntity> task, Predicate<? super Task<?>> taskTester) {
        return new BrainTaskHelper(task, taskTester);
    }

    public static List<BrainTaskHelper> singleton(Task<? super MobEntity> task) {
        return Collections.singletonList(of(task));
    }

    public static List<BrainTaskHelper> singleton(Task<? super MobEntity> task, Predicate<? super Task<?>> taskTester) {
        return Collections.singletonList(of(task, taskTester));
    }

    public static Predicate<? super Task<?>> replace(Class<?> clazz) {
        return t -> clazz.isAssignableFrom(t.getClass());
    }

    public static Predicate<? super Task<?>> override() {
        return t -> false;
    }

    public static Predicate<? super Task<?>> replaceAll(Class<?>... classes) {
        return t -> Arrays.stream(classes).anyMatch(clazz -> clazz.isAssignableFrom(t.getClass()));
    }

    public Task<? super MobEntity> getTask() {
        return task;
    }

    /**
     * If true, remove the task
     */
    @Override
    public boolean test(Task<?> task) {
        return taskTester.test(task);
    }
}
