<template>
  <div class="user-permission-manager">
    <div class="header">
      <h2>{{ t('userPermission.title') }}</h2>
      <div class="search-box">
        <jm-input
          v-model="searchKeyword"
          :placeholder="t('userPermission.searchPlaceholder')"
          prefix-icon="jm-icon-button-search"
          clearable
          @keyup.enter="searchUsers"
        />
        <jm-button type="primary" @click="searchUsers">{{ t('common.search') }}</jm-button>
      </div>
    </div>

    <div class="user-list" v-loading="loading">
      <jm-empty v-if="filteredUsers.length === 0" />

      <div v-else class="user-items">
        <div v-for="user in filteredUsers" :key="user.id" class="user-item">
          <div class="user-info">
            <div class="user-avatar">
              <img v-if="user.avatarUrl" :src="user.avatarUrl" alt="avatar" />
              <span v-else>{{ user.username?.charAt(0).toUpperCase() }}</span>
            </div>
            <div class="user-details">
              <div class="user-name">{{ user.nickname || user.username }}</div>
              <div class="user-meta">
                <span>{{ user.username }}</span>
                <span>{{ user.email }}</span>
              </div>
            </div>
          </div>

          <div class="user-roles">
            <div class="roles-title">{{ t('userPermission.assignedRoles') }}</div>
            <div class="roles-list">
              <el-tag
                v-for="role in user.roles"
                :key="role.id"
                closable
                @close="removeRoleFromUser(user.id, role.id)"
              >
                {{ role.name }}
              </el-tag>
              <jm-button size="small" @click="showRoleDialog(user)">
                {{ t('userPermission.assignRole') }}
              </jm-button>
            </div>
          </div>

          <div class="user-actions">
            <jm-button size="small" @click="viewUserPermissions(user)">
              {{ t('userPermission.viewPermissions') }}
            </jm-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分配角色对话框 -->
    <jm-dialog v-model="showRoleDialogFlag" :title="t('userPermission.assignRole')" width="500px">
      <div class="role-selection">
        <div
          v-for="role in allRoles"
          :key="role.id"
          :class="['role-option', { selected: selectedRoleIds.includes(role.id) }]"
          @click="toggleRole(role.id)"
        >
          <div class="role-name">{{ role.name }}</div>
          <div class="role-desc">{{ role.description }}</div>
        </div>
      </div>

      <template #footer>
        <jm-button @click="showRoleDialogFlag = false">{{ t('common.cancel') }}</jm-button>
        <jm-button type="primary" @click="saveUserRoles" :loading="saving">
          {{ t('common.save') }}
        </jm-button>
      </template>
    </jm-dialog>

    <!-- 用户权限详情对话框 -->
    <jm-dialog v-model="showPermissionDialog" :title="t('userPermission.permissionDetails')" width="700px">
      <div class="permission-detail">
        <div v-for="group in permissionGroups" :key="group.type" class="permission-group">
          <div class="group-title">{{ t(`permission.type.${group.type}`) }}</div>
          <div class="permission-tags">
            <el-tag
              v-for="perm in group.permissions"
              :key="perm.id"
              :type="perm.checked ? 'success' : 'info'"
            >
              {{ perm.name }}
            </el-tag>
          </div>
        </div>
      </div>
    </jm-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, onMounted, ref, computed } from 'vue';
import { fetchUsers, fetchUserRoles, assignUserRoles, fetchUserPermissions, fetchRoles } from '@/api/permission';
import { ElMessage } from 'element-plus';
import { useLocale } from '@/utils/i18n';

export default defineComponent({
  setup() {
    const { t } = useLocale();
    const { proxy } = getCurrentInstance() as any;

    const loading = ref<boolean>(false);
    const saving = ref<boolean>(false);
    const users = ref<any[]>([]);
    const allRoles = ref<any[]>([]);
    const searchKeyword = ref<string>('');
    const showRoleDialogFlag = ref<boolean>(false);
    const showPermissionDialog = ref<boolean>(false);
    const selectedUser = ref<any>(null);
    const selectedRoleIds = ref<string[]>([]);
    const userPermissions = ref<any[]>([]);

    const filteredUsers = computed(() => {
      if (!searchKeyword.value) {
        return users.value;
      }
      const keyword = searchKeyword.value.toLowerCase();
      return users.value.filter(
        (user) =>
          user.username?.toLowerCase().includes(keyword) ||
          user.nickname?.toLowerCase().includes(keyword) ||
          user.email?.toLowerCase().includes(keyword)
      );
    });

    const permissionGroups = computed(() => {
      const groups: Record<string, any[]> = {};

      userPermissions.value.forEach((perm) => {
        const type = perm.resourceType || 'SYSTEM';
        if (!groups[type]) {
          groups[type] = [];
        }
        groups[type].push({ ...perm, checked: true });
      });

      return Object.keys(groups).map((type) => ({
        type,
        permissions: groups[type],
      }));
    });

    const loadUsers = async () => {
      loading.value = true;
      try {
        users.value = await fetchUsers();

        for (const user of users.value) {
          try {
            const roles = await fetchUserRoles(user.id);
            user.roles = roles;
          } catch {
            user.roles = [];
          }
        }
      } catch (err) {
        proxy.$throw(err, proxy);
      } finally {
        loading.value = false;
      }
    };

    const loadRoles = async () => {
      try {
        allRoles.value = await fetchRoles();
      } catch (err) {
        proxy.$throw(err, proxy);
      }
    };

    const searchUsers = async () => {
      await loadUsers();
    };

    const showRoleDialog = async (user: any) => {
      selectedUser.value = user;
      selectedRoleIds.value = user.roles?.map((r: any) => r.id) || [];
      showRoleDialogFlag.value = true;
    };

    const toggleRole = (roleId: string) => {
      const index = selectedRoleIds.value.indexOf(roleId);
      if (index === -1) {
        selectedRoleIds.value.push(roleId);
      } else {
        selectedRoleIds.value.splice(index, 1);
      }
    };

    const saveUserRoles = async () => {
      saving.value = true;
      try {
        await assignUserRoles(selectedUser.value.id, selectedRoleIds.value);
        ElMessage.success(t('userPermission.assignSuccess'));

        const updatedRoles = allRoles.value.filter((r) => selectedRoleIds.value.includes(r.id));
        selectedUser.value.roles = updatedRoles;

        showRoleDialogFlag.value = false;
      } catch (err) {
        proxy.$throw(err, proxy);
      } finally {
        saving.value = false;
      }
    };

    const removeRoleFromUser = async (userId: string, roleId: string) => {
      try {
        const user = users.value.find((u) => u.id === userId);
        if (user) {
          const newRoleIds = user.roles.filter((r: any) => r.id !== roleId).map((r: any) => r.id);
          await assignUserRoles(userId, newRoleIds);
          ElMessage.success(t('userPermission.removeSuccess'));
          user.roles = user.roles.filter((r: any) => r.id !== roleId);
        }
      } catch (err) {
        proxy.$throw(err, proxy);
      }
    };

    const viewUserPermissions = async (user: any) => {
      selectedUser.value = user;
      try {
        userPermissions.value = await fetchUserPermissions(user.id);
        showPermissionDialog.value = true;
      } catch (err) {
        proxy.$throw(err, proxy);
      }
    };

    onMounted(async () => {
      await loadUsers();
      await loadRoles();
    });

    return {
      t,
      loading,
      saving,
      users,
      allRoles,
      searchKeyword,
      filteredUsers,
      showRoleDialogFlag,
      showPermissionDialog,
      selectedUser,
      selectedRoleIds,
      userPermissions,
      permissionGroups,
      searchUsers,
      showRoleDialog,
      toggleRole,
      saveUserRoles,
      removeRoleFromUser,
      viewUserPermissions,
    };
  },
});
</script>

<style scoped lang="less">
.user-permission-manager {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      font-size: 18px;
      font-weight: bold;
      color: #082340;
      margin: 0;
    }

    .search-box {
      display: flex;
      gap: 10px;

      .el-input {
        width: 250px;
      }
    }
  }

  .user-items {
    display: flex;
    flex-direction: column;
    gap: 15px;

    .user-item {
      display: flex;
      align-items: center;
      padding: 20px;
      background: #fff;
      border: 1px solid #e8e8e8;
      border-radius: 4px;

      .user-info {
        display: flex;
        align-items: center;
        flex: 1;

        .user-avatar {
          width: 50px;
          height: 50px;
          border-radius: 50%;
          background: #096dd9;
          color: #fff;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 15px;
          font-size: 20px;
          overflow: hidden;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }

        .user-details {
          .user-name {
            font-size: 16px;
            font-weight: bold;
            color: #082340;
            margin-bottom: 5px;
          }

          .user-meta {
            display: flex;
            gap: 15px;
            font-size: 12px;
            color: #999;
          }
        }
      }

      .user-roles {
        flex: 2;
        margin-left: 30px;

        .roles-title {
          font-size: 12px;
          color: #999;
          margin-bottom: 8px;
        }

        .roles-list {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          align-items: center;
        }
      }

      .user-actions {
        margin-left: 20px;
      }
    }
  }

  .role-selection {
    max-height: 400px;
    overflow-y: auto;

    .role-option {
      padding: 15px;
      border: 1px solid #e8e8e8;
      border-radius: 4px;
      margin-bottom: 10px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        border-color: #096dd9;
      }

      &.selected {
        border-color: #096dd9;
        background: #e6f7ff;
      }

      .role-name {
        font-weight: bold;
        color: #082340;
        margin-bottom: 5px;
      }

      .role-desc {
        font-size: 12px;
        color: #999;
      }
    }
  }

  .permission-detail {
    max-height: 500px;
    overflow-y: auto;

    .permission-group {
      margin-bottom: 20px;

      .group-title {
        font-weight: bold;
        color: #082340;
        margin-bottom: 10px;
        padding-bottom: 5px;
        border-bottom: 1px solid #e8e8e8;
      }

      .permission-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
      }
    }
  }
}
</style>
