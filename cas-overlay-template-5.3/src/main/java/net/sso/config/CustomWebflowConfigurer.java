package net.sso.config;

import net.sso.entity.CustomCredential;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.configurer.AbstractCasWebflowConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * 表单配置类
 * 新增定义了验证码
 */
public class CustomWebflowConfigurer extends AbstractCasWebflowConfigurer {

    public CustomWebflowConfigurer(FlowBuilderServices flowBuilderServices, FlowDefinitionRegistry loginFlowDefinitionRegistry, ApplicationContext applicationContext, CasConfigurationProperties casProperties) {
        super(flowBuilderServices, loginFlowDefinitionRegistry, applicationContext, casProperties);
    }

    @Override
    protected void doInitialize() {
        final Flow flow = super.getLoginFlow();
        bindCredential(flow);
    }

    /**
     * 绑定自定义的Credential信息
     *
     * @param flow
     */
    protected void bindCredential(Flow flow) {

        // 重写绑定自定义credential
        createFlowVariable(flow, CasWebflowConstants.VAR_ID_CREDENTIAL, CustomCredential.class);

        // 登录页绑定新参数
        final ViewState state = (ViewState) flow.getState(CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
        final BinderConfiguration cfg = getViewStateBinderConfiguration(state);
        // 由于用户名以及密码已经绑定，所以只需对新加系统参数绑定即可
        // 字段名，转换器，是否必须字段
        cfg.addBinding(new BinderConfiguration.Binding("kaptcha", null, true));
    }
}
