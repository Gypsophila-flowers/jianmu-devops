<template>
  <div class="role-manager">
    <div class="right-top-btn">
      <router-link :to="{ name: 'index' }">
        <jm-button type="primary" class="jm-icon-button-cancel" size="small">
          {{ t('common.close') }}
        </jm-button>
      </router-link>
      <jm-button type="primary" class="jm-icon-button-add" size="small" @click="showCreateDialog = true">
        {{ t('role.create') }}
      </jm-button>
    </div>

    <div class="title">
      <span>{{ t('role.title') }}</span>
      <span class="desc">{{ t('role.total', { length: roles.length }) }}</span>
    </div>

    <div class="content" v-loading="loading">
      <jm-empty v-if="roles.length === 0"/>

      <div v-else class="role-list">
        <div v-for="role in roles" :key="role.id" class="role-item">
          <div class="role-info">
            <div class="role-header">
              <span class="role-name">{{ role.name }}</span>
              <span :class="['role-type', role.type.toLowerCase()]">{{ t(`role.type.${role.type}`) }}</span>
            </div>
            <div class="role-desc">{{ role.description || t('role.noDescription') }}</div>
            <div class="role-meta">
              <span>{{ t('role.code') }}: {{ role.code }}</span>
              <span>{{ t('role.status') }}: {{ role.status ? t('role.enabled') : t('role.disabled') }}</span>
            </div>
          </div>

          <div class="role-actions">
            <jm-button size="small" class="edit-btn" @click="editRole(role)">
              {{ t('role.edit') }}
            </jm-button>
            <jm-button
              v-if="role.type === 'CUSTOM'"
              size="small"
              class="delete-btn"
              @click="confirmDelete(role)"
            >
              {{ t('role.delete') }}
            </jm-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建/编辑角色对话框 -->
    <jm-dialog
      v-model="showCreateDialog"
      :title="editingRole ? t('role.edit') : t('role.create')"
      width="600px"
    >
      <jm-form :model="roleForm" :rules="roleRules" ref="roleFormRef">
        <jm-form-item :label="t('role.form.name')" prop="name">
          <jm-input v-model="roleForm.name" :placeholder="t('role.form.namePlaceholder')" />
        </jm-form-item>

        <jm-form-item :label="t('role.form.code')" prop="code">
          <jm-input
            v-model="roleForm.code"
            :placeholder="t('role.form.codePlaceholder')"
            :disabled="!!editingRole"
          />
        </jm-form-item>

        <jm-form-item :label="t('role.form.description')" prop="description">
          <jm-input
            v-model="roleForm.description"
            type="textarea"
            :rows="3"
            :placeholder="t('role.form.descriptionPlaceholder')"
          />
        </jm-form-item>

        <jm-form-item :label="t('role.form.permissions')">
          <div class="permission-tree">
            <el-tree
              ref="permissionTreeRef"
              :data="permissionTree"
              :props="treeProps"
              node-key="id"
              :default-expand-all="true"
              show-checkbox
              :check-strictly="false"
            >
              <template #default="{ node, data }">
                <span class="permission-node">
                  <span>{{ data.name }}</span>
                  <span class="permission-code">{{ data.code }}</span>
                </span>
              </template>
            </el-tree>
          </div>
        </jm-form-item>
      </jm-form>

      <template #footer>
        <jm-button @click="showCreateDialog = false">{{ t('common.cancel') }}</jm-button>
        <jm-button type="primary" @click="saveRole" :loading="saving">
          {{ t('common.save') }}
        </jm-button>
      </template>
    </jm-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, onMounted, ref } from 'vue';
import { fetchRoles, createRole, updateRole, deleteRole, fetchPermissions, fetchRolePermissions } from '@/api/permission';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useLocale } from '@/utils/i18n';

export default defineComponent({
  setup() {
    const { t } = useLocale();
    const { proxy } = getCurrentInstance() as any;

    const loading = ref<boolean>(false);
    const saving = ref<boolean>(false);
    const roles = ref<any[]>([]);
    const permissionTree = ref<any[]>([]);
    const showCreateDialog = ref<boolean>(false);
    const editingRole = ref<any>(null);
    const roleFormRef = ref<any>(null);
    const permissionTreeRef = ref<any>(null);

    const roleForm = ref({
      name: '',
      code: '',
      description: '',
    });

    const roleRules = {
      name: [{ required: true, message: t('role.validation.nameRequired'), trigger: 'blur' }],
      code: [{ required: true, message: t('role.validation.codeRequired'), trigger: 'blur' }],
    };

    const treeProps = {
      children: 'children',
      label: 'name',
    };

    const loadRoles = async () => {
      loading.value = true;
      try {
        roles.value = await fetchRoles();
      } catch (err) {
        proxy.$throw(err, proxy);
      } finally {
        loading.value = false;
      }
    };

    const loadPermissions = async () => {
      try {
        permissionTree.value = await fetchPermissions();
      } catch (err) {
        proxy.$throw(err, proxy);
      }
    };

    const editRole = async (role: any) => {
      editingRole.value = role;
      roleForm.value = {
        name: role.name,
        code: role.code,
        description: role.description,
      };

      try {
        const permissions = await fetchRolePermissions(role.id);
        const checkedIds = permissions.map((p: any) => p.id);
        permissionTreeRef.value?.setCheckedKeys(checkedIds);
      } catch (err) {
        proxy.$throw(err, proxy);
      }

      showCreateDialog.value = true;
    };

    const saveRole = async () => {
      await roleFormRef.value?.validate();

      saving.value = true;
      try {
        const checkedPermissions = permissionTreeRef.value?.getCheckedKeys() || [];
        const halfCheckedPermissions = permissionTreeRef.value?.getHalfCheckedKeys() || [];
        const allPermissions = [...checkedPermissions, ...halfCheckedPermissions];

        const dto = {
          ...roleForm.value,
          permissionIds: allPermissions,
          status: true,
        };

        if (editingRole.value) {
          await updateRole(editingRole.value.id, dto);
          ElMessage.success(t('role.updateSuccess'));
        } else {
          await createRole(dto);
          ElMessage.success(t('role.createSuccess'));
        }

        showCreateDialog.value = false;
        editingRole.value = null;
        await loadRoles();
      } catch (err) {
        proxy.$throw(err, proxy);
      } finally {
        saving.value = false;
      }
    };

    const confirmDelete = async (role: any) => {
      try {
        await ElMessageBox.confirm(
          t('role.deleteConfirm', { name: role.name }),
          t('common.warning'),
          {
            confirmButtonText: t('common.confirm'),
            cancelButtonText: t('common.cancel'),
            type: 'warning',
          }
        );

        await deleteRole(role.id);
        ElMessage.success(t('role.deleteSuccess'));
        await loadRoles();
      } catch (err) {
        if (err !== 'cancel') {
          proxy.$throw(err, proxy);
        }
      }
    };

    onMounted(async () => {
      await loadRoles();
      await loadPermissions();
    });

    return {
      t,
      loading,
      saving,
      roles,
      permissionTree,
      showCreateDialog,
      editingRole,
      roleFormRef,
      roleForm,
      roleRules,
      treeProps,
      permissionTreeRef,
      editRole,
      saveRole,
      confirmDelete,
    };
  },
});
</script>

<style scoped lang="less">
.role-manager {
  .title {
    margin-bottom: 20px;
    font-size: 16px;
    font-weight: bold;
    color: #082340;

    .desc {
      margin-left: 10px;
      font-size: 14px;
      font-weight: normal;
      color: #6b7b8d;
    }
  }

  .role-list {
    display: flex;
    flex-direction: column;
    gap: 15px;

    .role-item {
      padding: 20px;
      background: #fff;
      border: 1px solid #e8e8e8;
      border-radius: 4px;

      .role-info {
        flex: 1;

        .role-header {
          display: flex;
          align-items: center;
          gap: 10px;
          margin-bottom: 8px;

          .role-name {
            font-size: 16px;
            font-weight: bold;
            color: #082340;
          }

          .role-type {
            padding: 2px 8px;
            font-size: 12px;
            border-radius: 2px;

            &.system {
              background: #e6f7ff;
              color: #1890ff;
            }

            &.custom {
              background: #f6ffed;
              color: #52c41a;
            }
          }
        }

        .role-desc {
          color: #6b7b8d;
          margin-bottom: 8px;
        }

        .role-meta {
          display: flex;
          gap: 20px;
          font-size: 12px;
          color: #999;
        }
      }

      .role-actions {
        display: flex;
        gap: 10px;
        margin-top: 15px;

        .edit-btn {
          background: #096dd9;
          color: #fff;
        }

        .delete-btn {
          background: #ff4d4f;
          color: #fff;
        }
      }
    }
  }

  .permission-tree {
    max-height: 300px;
    overflow-y: auto;
    border: 1px solid #e8e8e8;
    padding: 10px;

    .permission-node {
      display: flex;
      justify-content: space-between;
      width: 100%;

      .permission-code {
        color: #999;
        font-size: 12px;
        margin-left: 20px;
      }
    }
  }
}
</style>
