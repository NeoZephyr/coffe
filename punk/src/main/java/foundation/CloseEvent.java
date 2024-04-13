package foundation;

public class CloseEvent extends Event<String> {

    static enum PropagationStrategy {
        /**
         * 微服务的多个实例只收到一次消息/任务
         * e.g. entityChanged: 其他微服务跟进做修改, sendEmail: 发送邮件
         */
        PER_SERVICE,
        /**
         * 微服务的每个实例都收到一次消息/任务
         * e.g. configChanged: 所有实例都跟进刷新配置, invalidateCache：所有实例都刷新缓存
         */
        PER_INSTANCE;
    }

    public PropagationStrategy strategy = PropagationStrategy.PER_INSTANCE;

    @Override
    String getValue() {
        return "abc";
    }
}
