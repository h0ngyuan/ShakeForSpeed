<template>
  <el-form ref="taskFormRef" :model="form" :rules="rules" label-width="100px">
    <el-form-item label="任务名称" prop="name">
      <el-input v-model="form.name" placeholder="请输入任务名称" />
    </el-form-item>
    
    <el-form-item label="任务地点" prop="location">
      <el-input v-model="form.location" placeholder="请输入任务地点" />
    </el-form-item>
    
    <el-form-item label="开始时间" prop="startTime">
      <el-time-picker
        v-model="form.startTime"
        placeholder="选择开始时间"
        format="HH:mm"
        style="width: 100%;"
      />
    </el-form-item>
    
    <el-form-item label="持续时间" prop="duration">
      <div style="display: flex; gap: 10px; align-items: center;">
        <el-input-number
          v-model="durationMinutes"
          :min="0"
          :max="5"
          placeholder="分钟"
          style="width: 100px;"
        />
        <span>分</span>
        <el-input-number
          v-model="durationSeconds"
          :min="0"
          :max="59"
          placeholder="秒"
          style="width: 100px;"
        />
        <span>秒</span>
      </div>
    </el-form-item>
    
    <el-form-item label="奖励规则" style="margin-bottom: 20px;">
      <el-button type="primary" size="small" @click="addRewardRule" style="margin-bottom: 15px; background-color: #1677ff; border-color: #1677ff;">+ 新增奖励</el-button>
      <el-table v-model:data="form.rewardRules" border style="width: 100%; margin-bottom: 20px; border: 1px solid #ff4d4f;">
        <el-table-column label="奖励区间" width="200">
          <template #default="{ row, $index }">
            <div style="display: flex; gap: 10px; align-items: center;">
              <el-input-number v-model="row.range.start" :min="1" controls-position="right" style="width: 80px;" />
              <span>-</span>
              <el-input-number v-model="row.range.end" :min="row.range.start" controls-position="right" style="width: 80px;" />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="奖励">
          <template #default="{ row, $index }">
            <div style="display: flex; gap: 10px; align-items: center;">
              <el-input v-model="row.reward" placeholder="请输入奖励内容" style="flex: 1;" />
              <el-button type="danger" size="small" @click="removeRewardRule($index)" style="margin-left: 10px;">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';

interface RewardRule {
  type: 'range';
  range: { start: number; end: number };
  reward: string;
}

export interface TaskForm {
  name: string;
  location: string;
  startTime: string | Date | undefined;
  duration: number;
  rewardRules: RewardRule[];
}

const props = defineProps<{
  initialData?: Partial<TaskForm>;
}>();

const emit = defineEmits<{
  (e: 'submit', formData: TaskForm): void;
}>();

const taskFormRef = ref<FormInstance>();
const durationMinutes = ref(Math.floor((props.initialData?.duration || 300) / 60));
const durationSeconds = ref((props.initialData?.duration || 300) % 60);

const form = reactive<TaskForm>({
  name: props.initialData?.name || '',
  location: props.initialData?.location || '',
  startTime: props.initialData?.startTime ? new Date(props.initialData.startTime) : undefined,
  duration: props.initialData?.duration || 300,
  rewardRules: props.initialData?.rewardRules || [{
    type: 'range',
  range: { start: 1, end: 1 },
  reward: ''
  }]
});

const addRewardRule = () => {
  form.rewardRules.push({
      type: 'range',
      range: { start: 1, end: 1 },
      reward: ''
    });
};

const removeRewardRule = (index: number) => {
  if (form.rewardRules.length > 1) {
    form.rewardRules.splice(index, 1);
  } else {
    ElMessage.warning('至少保留一条奖励规则');
  }
};

onMounted(() => {
  if (props.initialData) {
    Object.assign(form, props.initialData);
  }
});

const rules: FormRules = {
  name: [
    { required: true, message: '请输入任务名称', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入任务地点', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  duration: [
    { required: true, message: '请输入持续时间', trigger: 'blur' },
    { type: 'number', min: 60, max: 3660, message: '持续时间必须在60-3660秒之间', trigger: 'blur' }
  ]
};





const validateRewardRules = () => {
  for (let i = 0; i < form.rewardRules.length; i++) {
    const rule = form.rewardRules[i];
    if (!rule.reward) {
      ElMessage.error(`第${i+1}条奖励规则的奖励内容不能为空`);
      return false;
    }
    
    if (rule.type === 'range' && (!rule.range || rule.range.start >= rule.range.end)) {
      ElMessage.error(`第${i+1}条奖励规则的区间范围无效`);
      return false;
    }
  }
  return true;
};

const validateAndSubmit = () => {
  taskFormRef.value?.validate((valid) => {
    if (valid && validateRewardRules()) {
      const formData = {
        ...form,
        duration: durationMinutes.value * 60 + durationSeconds.value,
        startTime: form.startTime instanceof Date ? form.startTime.toISOString() : form.startTime
      };
      emit('submit', formData);
    }
  });
};

// 暴露验证提交方法给父组件
defineExpose({
  validateAndSubmit
});
</script>