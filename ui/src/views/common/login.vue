<template>
  <div class="login">
    <div class="number-login" v-if="!loginType">
      <div class="desc">
        <span>{{ t('login.welcome') }}</span>
        <div class="tip">
          <slot name="tip"></slot>
        </div>
      </div>
      <jm-form :model="loginForm" :rules="loginRule" ref="loginFormRef">
        <div class="item">
          <jm-form-item prop="username">
            <jm-input
              v-model="loginForm.username"
              prefix-icon="jm-icon-input-user"
              clearable
              :placeholder="t('login.usernamePlaceholder')"
              @keyup.enter="login"
            />
          </jm-form-item>
        </div>
        <div class="item">
          <jm-form-item prop="password">
            <jm-input
              v-model="loginForm.password"
              prefix-icon="jm-icon-input-lock"
              type="password"
              clearable
              show-password
              :placeholder="t('login.passwordPlaceholder')"
              @keyup.enter="login"
            />
          </jm-form-item>
        </div>
        <div class="item remember-row">
          <jm-checkbox v-model="loginForm.remember" @keyup.enter="login">
            <span class="label">{{ t('login.remember') }}</span>
          </jm-checkbox>
          <a href="javascript:void(0)" class="forgot-password" @click="$emit('change-password')">
            {{ t('login.forgotPassword') }}
          </a>
        </div>
        <div class="btn">
          <jm-button type="primary" @click="login" :loading="loading">{{ t('login.login') }}</jm-button>
        </div>
      </jm-form>
      
      <!-- 注册入口 -->
      <div class="register-link">
        {{ t('login.noAccount') }}
        <a href="javascript:void(0)" @click="$emit('switch-to-register')">{{ t('login.registerNow') }}</a>
      </div>
    </div>
    <div v-else-if="authError" class="error-login">
      <div class="logo">
        <div class="img"></div>
      </div>
      <div class="tip">{{ t('login.loginProblem') }}</div>
      <div class="operations">
        <jm-button class="btn cancel" @click="$emit('cancel')">{{ t('login.cancel') }}</jm-button>
        <jm-button type="primary" class="btn" @click="fetchThirdAuthUrl">{{ t('login.reLogin') }}</jm-button>
      </div>
    </div>
    <div :class="[`${loginType.toLowerCase()}-login`, loading ? 'loading' : '']" @click="fetchThirdAuthUrl" v-else>
      <div class="logo">
        <div class="img" v-if="!loading"></div>
        <div class="loading" v-else></div>
      </div>
      <span class="tip">{{
        loading ? t('login.loggingInWith', { type: Type }) : t('login.loginWith', { type: Type })
      }}</span>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, getCurrentInstance, onBeforeUnmount, onMounted, ref } from 'vue';
import { createNamespacedHelpers, mapActions, useStore } from 'vuex';
import { ILoginForm, IState } from '@/model/modules/session';
import { namespace } from '@/store/modules/session';
import { useRoute, useRouter } from 'vue-router';
import { AUTHORIZE_INDEX, PLATFORM_INDEX } from '@/router/path-def';
import { fetchAuthUrl } from '@/api/session';
import { getRedirectUri } from '@/utils/redirect-uri';
import { useLocale } from '@/utils/i18n';

const { mapActions: mapSessionActions, mapMutations } = createNamespacedHelpers(namespace);

export default defineComponent({
  emits: ['logined', 'cancel', 'switch-to-register', 'change-password'],
  props: {
    code: String,
    error_description: String,
    gitRepo: String,
    gitRepoOwner: String,
    type: {
      type: String,
      default: 'index',
    },
  },
  setup(props: any, { emit }) {
    const { t } = useLocale();
    const { proxy } = getCurrentInstance() as any;
    const router = useRouter();
    const route = useRoute();
    const store = useStore();
    const loginType = ref<string>(store.state.thirdPartyType);
    const Type = computed<string>(() => {
      switch (loginType.value) {
        case 'GITEE':
          return 'Gitee';
        case 'GITLINK':
          return 'GitLink';
        case 'GITLAB':
          return 'GitLab';
        case 'GITEA':
          return 'Gitea';
        default:
          return '';
      }
    });
    const { username, remember } = store.state[namespace] as IState;
    const loading = ref<boolean>(false);
    const loginFormRef = ref<any>(null);
    const loginForm = ref<ILoginForm>({
      username: remember ? username : '',
      password: '',
      remember,
    });
    const authError = ref<boolean>(false);
    
    const fetchThirdAuthUrl = async () => {
      authError.value = false;
      localStorage.setItem('temp-login-mode', props.type);
      localStorage.getItem('temp-login-mode') !== 'index' && (loading.value = true);
      try {
        const { authorizationUrl } = await fetchAuthUrl({
          thirdPartyType: loginType.value,
          redirectUri: getRedirectUri(props.gitRepo, props.gitRepoOwner),
        });
        window.open(authorizationUrl, localStorage.getItem('temp-login-mode') !== 'index' ? '_blank' : '_self');
      } catch (err) {
        proxy.$throw(err, proxy);
      }
    };
    
    const refreshState = async (e: any) => {
      if (e.key === 'session') {
        const newSession = JSON.parse(e.newValue)['_default'].session;
        if (store.state[namespace].session?.token) {
          loading.value = false;
          authError.value = false;
          setTimeout(() => {
            emit('logined');
          }, 500);
          return;
        }
        loading.value = false;
        authError.value = false;
        proxy.$success(t('login.success'));
        setTimeout(() => {
          emit('logined');
        }, 500);
        proxy.mutateSession(newSession);
      }
      if (e.key === 'temp-login-error-message') {
        localStorage.getItem('temp-login-mode') !== 'index' && (authError.value = true);
        loading.value = false;
      }
    };
    
    onMounted(async () => {
      window.addEventListener('storage', refreshState);
      const dialogLogin = localStorage.getItem('temp-login-mode') !== 'index';
      if (props.error_description) {
        proxy.$error(props.error_description);
        localStorage.setItem('temp-login-error-message', props.error_description);
        dialogLogin &&
          setTimeout(() => {
            window.close();
          }, 2000);
        return;
      }
      if (props.code) {
        loading.value = true;
        try {
          await proxy.createOAuthSession({
            code: props.code,
            thirdPartyType: loginType.value,
            redirectUri: getRedirectUri(props.gitRepo, props.gitRepoOwner),
            gitRepo: props.gitRepo,
            gitRepoOwner: props.gitRepoOwner,
          });
          dialogLogin ? window.close() : await router.push(PLATFORM_INDEX);
        } catch (err) {
          proxy.$throw(err, proxy);
          localStorage.setItem('temp-login-error-message', err.message);
          dialogLogin &&
            setTimeout(() => {
              window.close();
            }, 2000);
        } finally {
          loading.value = false;
          emit('logined');
        }
        return;
      }

      if (route.path === AUTHORIZE_INDEX) {
        await fetchThirdAuthUrl();
      }
    });
    
    onBeforeUnmount(() => {
      window.onstorage = null;
    });
    
    return {
      t,
      authError,
      loginType,
      Type,
      fetchThirdAuthUrl,
      loading,
      loginFormRef,
      loginForm,
      loginRule: ref<Record<string, any>>({
        username: [{ required: true, message: t('login.usernameEmpty'), trigger: 'blur' }],
        password: [{ required: true, message: t('login.passwordEmpty'), trigger: 'blur' }],
      }),
      login: () => {
        loading.value = true;

        loginFormRef.value.validate(async (valid: boolean) => {
          if (!valid) {
            loading.value = false;
            return false;
          }
          try {
            await proxy.createSession({ ...loginForm.value });
            await proxy.initialize();
            if (route.name === 'login') {
              await router.push(PLATFORM_INDEX);
            }
          } catch (err) {
            proxy.$throw(err, proxy);
          } finally {
            loading.value = false;
            emit('logined');
          }
        });
      },
      ...mapActions(['initialize']),
      ...mapSessionActions({
        createSession: 'create',
        createOAuthSession: 'oauthLogin',
      }),
      ...mapMutations({
        mutateSession: 'oauthMutate',
      }),
    };
  },
});
</script>

<style scoped lang="less">
.login {
  box-sizing: border-box;
  margin: 0 auto;
  background-color: #ffffff;
  position: relative;

  .desc {
    margin-bottom: 20px;
    font-size: 14px;
    font-weight: bold;
    color: #082340;

    .tip {
      font-weight: 400;
      color: #082340;
      font-size: 14px;
      margin-top: 10px;
    }
  }

  .item {
    margin-bottom: 15px;

    .label {
      font-size: 13px;
      font-weight: 400;
      color: #6b7b8d;
    }

    &.remember-row {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .forgot-password {
        color: #096dd9;
        font-size: 13px;
        text-decoration: none;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  }

  .btn {
    padding-top: 15px;

    .el-button {
      width: 100%;
      letter-spacing: 5px;
    }

    .el-button--primary {
      box-shadow: none;
    }
  }

  .register-link {
    margin-top: 20px;
    text-align: center;
    font-size: 14px;
    color: #6b7b8d;

    a {
      color: #096dd9;
      text-decoration: none;
      margin-left: 5px;

      &:hover {
        text-decoration: underline;
      }
    }
  }

  .gitee-login,
  .gitlink-login,
  .gitlab-login,
  .gitea-login,
  .error-login {
    cursor: pointer;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    flex-direction: column;
    align-items: center;

    &.loading {
      pointer-events: none;
    }

    &:hover {
      .tip {
        color: #096dd9;
      }
    }

    .logo {
      .img {
        width: 56px;
        height: 56px;
        background: url('@/assets/svgs/logo/gitee.svg') no-repeat 100%;
      }

      .loading {
        width: 30px;
        height: 30px;
        background: url('@/assets/svgs/logo/loading.svg') no-repeat;
        animation: rotating 2s linear infinite;
      }
    }

    .tip {
      font-size: 14px;
      font-weight: 400;
      color: #012c53;
      margin-top: 20px;
    }
  }

  .gitlink-login {
    .logo {
      .img {
        width: 56px;
        height: 35px;
        background-image: url('@/assets/svgs/logo/gitlink.svg');
        background-size: cover;
        background-repeat: no-repeat;
      }
    }
  }

  .gitlab-login {
    .logo {
      .img {
        width: 56px;
        height: 56px;
        background-image: url('@/assets/svgs/logo/gitlab.svg');
        background-size: cover;
        background-repeat: no-repeat;
      }
    }
  }

  .gitea-login {
    .logo {
      .img {
        width: 56px;
        height: 56px;
        background-image: url('@/assets/svgs/logo/gitea.svg');
        background-size: cover;
        background-repeat: no-repeat;
      }
    }

    .tip {
      margin-top: 10px;
    }
  }

  .error-login {
    cursor: default;
    width: 100%;

    &:hover {
      .tip {
        color: #012c53;
      }
    }

    .operations {
      display: flex;
      margin-top: 30px;

      .btn {
        cursor: pointer;
        padding: 8px 24px;
        text-align: center;
        border-radius: 2px;
        font-size: 14px;
        font-weight: 400;
        border: none;
        margin: 0;

        &.cancel {
          &:hover {
            background-color: #eeeeee;
          }

          color: #082340;
          background-color: #f5f5f5;
          margin-right: 40px;
        }
      }
    }

    .logo {
      .img {
        width: 56px;
        height: 56px;
        background-image: url('@/assets/svgs/logo/error.svg');
        background-size: cover;
        background-repeat: no-repeat;
      }
    }
  }
}
</style>