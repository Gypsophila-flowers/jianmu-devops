<template>
  <div class="change-password-drawer">
    <div class="drawer-header">
      <span class="title">{{ t('changePassword.title') }}</span>
      <jm-icon-button class="close-btn" icon="jm-icon-button-close" @click="$emit('close')" />
    </div>

    <div class="drawer-content">
      <jm-form :model="passwordForm" :rules="passwordRule" ref="passwordFormRef">
        <!-- 旧密码 -->
        <div class="form-item">
          <div class="label">{{ t('changePassword.oldPassword') }}</div>
          <jm-form-item prop="oldPassword">
            <jm-input
              v-model="passwordForm.oldPassword"
              prefix-icon="jm-icon-input-lock"
              type="password"
              clearable
              show-password
              :placeholder="t('changePassword.oldPasswordPlaceholder')"
            />
          </jm-form-item>
        </div>

        <!-- 新密码 -->
        <div class="form-item">
          <div class="label">{{ t('changePassword.newPassword') }}</div>
          <jm-form-item prop="newPassword">
            <jm-input
              v-model="passwordForm.newPassword"
              prefix-icon="jm-icon-input-lock"
              type="password"
              clearable
              show-password
              :placeholder="t('changePassword.newPasswordPlaceholder')"
            />
          </jm-form-item>
        </div>

        <!-- 确认新密码 -->
        <div class="form-item">
          <div class="label">{{ t('changePassword.confirmPassword') }}</div>
          <jm-form-item prop="confirmPassword">
            <jm-input
              v-model="passwordForm.confirmPassword"
              prefix-icon="jm-icon-input-lock"
              type="password"
              clearable
              show-password
              :placeholder="t('changePassword.confirmPasswordPlaceholder')"
              @keyup.enter="submit"
            />
          </jm-form-item>
        </div>

        <!-- 提交按钮 -->
        <div class="submit-btn">
          <jm-button @click="$emit('close')">{{ t('changePassword.cancel') }}</jm-button>
          <jm-button type="primary" @click="submit" :loading="loading" :disabled="loading">
            {{ t('changePassword.submit') }}
          </jm-button>
        </div>
      </jm-form>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, ref } from 'vue';
import { changePassword } from '@/api/session';
import { useLocale } from '@/utils/i18n';
import { ElMessage } from 'element-plus';

export default defineComponent({
  emits: ['close', 'success'],
  setup(props, { emit }) {
    const { t } = useLocale();
    const { proxy } = getCurrentInstance() as any;
    const passwordFormRef = ref<any>(null);
    const loading = ref<boolean>(false);

    const passwordForm = ref({
      oldPassword: '',
      newPassword: '',
      confirmPassword: '',
    });

    // 新密码验证器
    const validateNewPassword = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error(t('changePassword.newPasswordEmpty')));
      } else if (value.length < 6) {
        callback(new Error(t('changePassword.passwordTooShort')));
      } else if (value === passwordForm.value.oldPassword) {
        callback(new Error(t('changePassword.passwordSame')));
      } else {
        if (passwordForm.value.confirmPassword !== '') {
          passwordFormRef.value.validateField('confirmPassword');
        }
        callback();
      }
    };

    // 确认密码验证器
    const validateConfirmPassword = (rule: any, value: any, callback: any) => {
      if (value === '') {
        callback(new Error(t('changePassword.confirmPasswordEmpty')));
      } else if (value !== passwordForm.value.newPassword) {
        callback(new Error(t('changePassword.passwordNotMatch')));
      } else {
        callback();
      }
    };

    const passwordRule = ref<Record<string, any>>({
      oldPassword: [
        { required: true, message: t('changePassword.oldPasswordEmpty'), trigger: 'blur' },
      ],
      newPassword: [
        { required: true, validator: validateNewPassword, trigger: 'blur' },
      ],
      confirmPassword: [
        { required: true, validator: validateConfirmPassword, trigger: 'blur' },
      ],
    });

    const submit = () => {
      passwordFormRef.value.validate(async (valid: boolean) => {
        if (!valid) {
          return false;
        }

        loading.value = true;

        try {
          await changePassword({
            oldPassword: passwordForm.value.oldPassword,
            newPassword: passwordForm.value.newPassword,
          });

          ElMessage.success(t('changePassword.success'));
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
      passwordFormRef,
      passwordForm,
      passwordRule,
      submit,
      loading,
    };
  },
});
</script>

<style scoped lang="less">
.change-password-drawer {
  padding: 20px;
  background: #fff;
  border-radius: 4px;

  .drawer-header {
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

  .drawer-content {
    .form-item {
      margin-bottom: 15px;

      .label {
        font-size: 14px;
        color: #082340;
        margin-bottom: 8px;
      }
    }

    .submit-btn {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
      gap: 10px;

      .el-button {
        min-width: 80px;
      }
    }
  }
}
</style>