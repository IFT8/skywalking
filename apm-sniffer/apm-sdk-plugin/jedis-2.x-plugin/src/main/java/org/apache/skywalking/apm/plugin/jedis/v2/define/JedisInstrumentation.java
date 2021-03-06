/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package org.apache.skywalking.apm.plugin.jedis.v2.define;

import java.net.URI;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import org.apache.skywalking.apm.plugin.jedis.v2.JedisConstructorWithShardInfoArgInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.plugin.jedis.v2.JedisConstructorWithUriArgInterceptor;
import org.apache.skywalking.apm.plugin.jedis.v2.JedisMethodInterceptor;
import org.apache.skywalking.apm.plugin.jedis.v2.RedisMethodMatch;

import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static org.apache.skywalking.apm.agent.core.plugin.bytebuddy.ArgumentTypeNameMatch.takesArgumentWithType;
import static org.apache.skywalking.apm.agent.core.plugin.match.NameMatch.byName;

/**
 * {@link JedisInstrumentation} presents that skywalking intercept all constructors and methods of {@link
 * redis.clients.jedis.Jedis}. {@link JedisConstructorWithShardInfoArgInterceptor} intercepts all constructor with
 * argument {@link redis.clients.jedis.HostAndPort} ,{@link JedisConstructorWithUriArgInterceptor} intercepts the
 * constructors with uri argument and the other constructor intercept by class {@link
 * JedisConstructorWithShardInfoArgInterceptor}. {@link JedisMethodInterceptor} intercept all methods of {@link
 * redis.clients.jedis.Jedis}.
 *
 * @author zhangxin
 */
public class JedisInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String HOST_AND_PORT_ARG_TYPE_NAME = "redis.clients.jedis.HostAndPort";
    private static final String ENHANCE_CLASS = "redis.clients.jedis.Jedis";
    private static final String CONSTRUCTOR_WITH_STRING_ARG_INTERCEPT_CLASS = "JedisConstructorWithStringArgInterceptor";
    private static final String CONSTRUCTOR_WITH_SHARD_INFO_ARG_INTERCEPT_CLASS = "JedisConstructorWithShardInfoArgInterceptor";
    private static final String CONSTRUCTOR_WITH_URI_ARG_INTERCEPT_CLASS = "JedisConstructorWithUriArgInterceptor";
    private static final String JEDIS_METHOD_INTERCET_CLASS = "JedisMethodInterceptor";

    @Override
    public ClassMatch enhanceClass() {
        return byName(ENHANCE_CLASS);
    }

    @Override
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[] {
            new ConstructorInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getConstructorMatcher() {
                    return takesArgument(0, String.class);
                }

                @Override
                public String getConstructorInterceptor() {
                    return CONSTRUCTOR_WITH_STRING_ARG_INTERCEPT_CLASS;
                }
            },
            new ConstructorInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getConstructorMatcher() {
                    return takesArgumentWithType(0, HOST_AND_PORT_ARG_TYPE_NAME);
                }

                @Override
                public String getConstructorInterceptor() {
                    return CONSTRUCTOR_WITH_SHARD_INFO_ARG_INTERCEPT_CLASS;
                }
            },
            new ConstructorInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getConstructorMatcher() {
                    return takesArgument(0, URI.class);
                }

                @Override
                public String getConstructorInterceptor() {
                    return CONSTRUCTOR_WITH_URI_ARG_INTERCEPT_CLASS;
                }
            }
        };
    }

    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
            new InstanceMethodsInterceptPoint() {
                @Override
                public ElementMatcher<MethodDescription> getMethodsMatcher() {
                    return RedisMethodMatch.INSTANCE.getJedisMethodMatcher();
                }

                @Override
                public String getMethodsInterceptor() {
                    return JEDIS_METHOD_INTERCET_CLASS;
                }

                @Override public boolean isOverrideArgs() {
                    return false;
                }
            }
        };
    }
}
