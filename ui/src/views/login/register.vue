<template>
  <div class="register-drawer">
    <div class="register-header">
      <span class="title">{{ t('register.title') }}</span>
      <jm-icon-button class="close-btn" icon="jm-icon-button-close" @click="$emit('close')" />
    </div>

    <div class="register-content">
      <jm-form :model="registerForm" :rules="registerRule" ref="registerFormRef">
        <!-- 用户名 -->
        <div class="form-item">
          <div class="label">{{ t('register.username') }}</div>
          <jm-form-item prop="username">
            <jm-input
              v-model="registerForm.username"
              prefix-icon="jm-icon-input-user"
              clearable
              :placeholder="t('register.usernamePlaceholder')"
            />
          </jm-form-item>
        </div>

        <!-- 邮箱 -->
        <div class="form-item">
          <div class="label">{{ t('register.email') }}</div>
          <jm-form-item prop="email">
            <jm-input
              v-model="registerForm.email"
              prefix-icon="jm-icon-input-user"
              clearable
              :placeholder="t('register.emailPlaceholder')"
            />
          </jm-form-item>
        </div>

        <!-- 密码 -->
        <div class="form-item">
          <div class="label">{{ t('register.password') }}</div>
          <jm-form-item prop="password">
            <jm-input
              v-model="registerForm.password"
              prefix-icon="jm-icon-input-lock"
              type="password"
              clearable
              show-password
              :placeholder="t('register.passwordPlaceholder')"
            />
          </jm-form-item>
        </div>

        <!-- 确认密码 -->
        <div class="form-item">
          <div class="label">{{ t('register.confirmPassword') }}</div>
          <jm-form-item prop="confirmPassword">
            <jm-input
              v-model="registerForm.confirmPassword"
              prefix-icon="jm-icon-input-lock"
              type="password"
              clearable
              show-password
              :placeholder="t('register.confirmPasswordPlaceholder')"
              @keyup.enter="submit"
            />
          </jm-form-item>
        </div>

        <!-- 昵称 -->
        <div class="form-item">
          <div class="label">{{ t('register.nickname') }} <span class="optional">({{ t('register.optional') }})</span></div>
          <jm-form-item prop="nickname">
            <jm-input
              v-model="registerForm.nickname"
              clearable
              :placeholder="t('register.nicknamePlaceholder')"
            />
          </jm-form-item>
        </div>

        <!-- 提交按钮 -->
        <div class="submit-btn">
          <jm-button type="primary" @click="submit" :loading="loading" :disabled="loading">
            {{ t('register.submit') }}
          </jm-button>
        </div>
      </jm-form>

      <div class="login-link">
        {{ t('register.hasAccount') }}
        <a href="javascript:void(0)" @click="$emit('switch-to-login')">{{ t('register.goLogin') }}</a>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, ref } from 'vue';
import { register } from '@/api/session';
import { useLocale } from '@/utils/i18n';
import { ElMessage } from 'element-plus';

export default defineComponent({
  emits: ['close', 'switch-to-login', 'success'],
  setup(props, { emit }) {
    const { t } = useLocale();
    const { proxy } = getCurrentInstance() as any;
    const registerFormRef = ref<any>(null);
    const loading = ref<boolean>(false);

    const registerForm = ref({
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      nickname: '',
    });

    // 密码验证器
    const validatePassword = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error(t('register.passwordEmpty')));
      } else if (value.length < 6) {
        callback(new Error(t('register.passwordTooShort')));
      } else {
        if (registerForm.value.confirmPassword !== '') {
          registerFormRef.value.validateField('confirmPassword');
        }
        callback();
      }
    };

    // 确认密码验证器
    const validateConfirmPassword = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error(t('register.confirmPasswordEmpty')));
      } else if (value !== registerForm.value.password) {
        callback(new Error(t('register.passwordNotMatch')));
      } else {
        callback();
      }
    };

    const registerRule = ref<Record<string, any>>({
      username: [
        { required: true, message: t('register.usernameEmpty'), trigger: 'blur' },
        { min: 4, max: 50, message: t('register.usernameLength'), trigger: 'blur' },
      ],
      email: [
        { required: true, message: t('register.emailEmpty'), trigger: 'blur' },
        { type: 'email', message: t('register.emailInvalid'), trigger: 'blur' },
      ],
      password: [
        { required: true, validator: validatePassword, trigger: 'blur' },
      ],
      confirmPassword: [
        { required: true, validator: validateConfirmPassword, trigger: 'blur' },
      ],
    });

    const submit = () => {
      registerFormRef.value.validate(async (valid: boolean) => {
        if (!valid) {
          return false;
        }

        loading.value = true;

        try {
          await register({
            username: registerForm.value.username,
            password: registerForm.value.password,
            email: registerForm.value.email,
            nickname: registerForm.value.nickname || registerForm.value.username,
          });

          ElMessage.success(t('register.success'));
          emit('success');
          emit('close');
        } catch (err: any) {
          proxy.$throw(err, proxy);
        } finally {
          loading.value = false;
        }
      });
    };

    return {
      t,
      registerFormRef,
      registerForm,
      registerRule,
      submit,
      loading,
    };
  },
});
</script>

<style scoped lang="less">
.register-drawer {
  padding: 20px;
  background: #fff;
  border-radius: 4px;

  .register-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .title {
      font-size: 18px;
      font-weight: bold;
      color: #082340;
    }

    .close-btn {
      border: none;
      background: transparent;
    }
  }

  .register-content {
    .form-item {
      margin-bottom: 15px;

      .label {
        font-size: 14px;
        color: #082340;
        margin-bottom: 8px;

        .optional {
          color: #6b7b8d;
          font-weight: normal;
        }
      }
    }

    .submit-btn {
      margin-top: 20px;

      .el-button {
        width: 100%;
        letter-spacing: 5px;
      }
    }

    .login-link {
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
  }
}
</style>