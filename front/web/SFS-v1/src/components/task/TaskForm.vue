<template>
  <el-form ref="taskFormRef" :model="form" :rules="rules" label-width="100px" style="width: 100%;  padding: 0 -30px;">
    <el-form-item label="任务名称" prop="activityName">
      <el-input v-model="form.activityName" placeholder="请输入任务名称" />
    </el-form-item>
    
    <el-form-item label="任务地点" prop="activityType">
      <el-input v-model="form.activityType" placeholder="请输入任务地点" />
    </el-form-item>
    
    <el-form-item label="开始时间" prop="beginTime">
      <el-time-picker
        v-model="form.beginTime"
        placeholder="选择开始时间"
        format="HH:mm"
        style="width: 100%;"
      />
    </el-form-item>
    <el-form-item label="持续时间" prop="durTime">
      <el-time-picker
        v-model="durationStr"
        :picker-options="{selectableRange: '00:00:00-00:01:00'}"
        format="mm:ss"
        value-format="mm:ss"
        placeholder="选择持续时间"
        style="width: 100%;"
      />
    </el-form-item>
    
    <el-form-item label="奖励设置">
      <div class="reward-container">
        <div v-for="(reward, index) in rewards" :key="index" class="reward-item">
          <el-row :gutter="10" class="reward-form-row">
            <el-col :lg="6">
              <el-form-item :prop="`rewards.${index}.name`" :rules="rewardRules.name">
                <el-input v-model="reward.name" placeholder="奖励名称" />
              </el-form-item>
            </el-col>
            <el-col :lg="8">
              <el-form-item :prop="`rewards.${index}.image`" :rules="rewardRules.image">
                <el-upload
                  class="upload-demo"
                  :on-preview="handlePreview"
                  :on-remove="handleRemove"
                  :before-remove="beforeRemove"
                  multiple
                  :limit="1"
                  :on-exceed="handleExceed"
                  :file-list="reward.fileList || []"
                  :auto-upload="false"
                  :on-change="(file) => handleFileChange(file, reward)"
                >
                  <el-button type="primary" size="small">上传图片</el-button>
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :lg="4">
              <el-form-item :prop="`rewards.${index}.rankStart`" :rules="rewardRules.rankStart">
                <el-input-number v-model="reward.rankStart" :min="0" controls-position="right" placeholder="起始排名" style="width:100%;" size="small" @change="handleRankChange" />
              </el-form-item>
            </el-col>
            <el-col :lg="2" style="display: flex; align-items: center; justify-content: center;">
              <span>-</span>
            </el-col>
            <el-col :lg="4">
              <el-form-item :prop="`rewards.${index}.rankEnd`" :rules="rewardRules.rankEnd">
                <el-input-number v-model="reward.rankEnd" :min="0" controls-position="right" placeholder="结束排名" style="width: 100%;" size="small" @change="handleRankChange" />
              </el-form-item>
            </el-col>
            <el-col :lg="2" class="delete-btn-col">
              <el-button type="danger" size="small" @click="removeReward(index)" v-if="rewards.length > 1">-</el-button>
            </el-col>
          </el-row>
        </div>
        <el-button type="primary" size="small" @click="addReward">+ 添加奖励</el-button>
      </div>
    </el-form-item>
    
    <el-form-item label="房间密码" prop="roomPwd">
      <el-input v-model="form.roomPwd" placeholder="请输入房间密码" style="width: 100%;" />
    </el-form-item>
    
    <el-form-item label="经度" prop="longitude">
      <el-input-number v-model="form.longitude" :min="-180" :max="180" placeholder="请输入经度" style="width: 100%;" />
    </el-form-item>
    
    <el-form-item label="纬度" prop="latitude">
      <el-input-number v-model="form.latitude" :min="-90" :max="90" placeholder="请输入纬度" style="width: 100%;" />
    </el-form-item>
    
    <el-form-item label="获取位置">
      <el-button type="primary" @click="getCurrentLocation">获取当前位置</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
// 声明百度地图相关类型
interface BMapGL {
  Geolocation: new () => BMapGeolocation;
  getStatus: () => number;
}

interface BMapGeolocation {
  getCurrentPosition: (successCallback: (result: any) => void, options?: any) => void;
  getStatus: () => number;
}

declare global {
  interface Window {
    BMap: BMapGL;
    BMAP_STATUS_SUCCESS: number;
    onBMapCallback: () => void;
  }
}

import { defineProps, defineEmits, ref, reactive, onMounted, computed, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import type { UploadFile, UploadUserFile, UploadStatus } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';

export interface Reward {
  id: string;
  name: string;
  image: string;
  rankStart: number;
  rankEnd: number;
  fileList?: UploadFile[];
  uploading?: boolean;
}

export interface Activity {
  id: number;
  activityName: string;
  activityType: string;
  creatorRole: string;
  beginTime?: string | Date;
  createTime: string;
  rewards: Reward[];
  durTime: number;
  roomPwd: number;
  longitude: number;
  latitude: number;
  status: 'not-started' | 'in-progress' | 'ended';
}

const props = defineProps<{
  initialData?: Partial<Activity>;
}>();

const emit = defineEmits<{
  (e: 'submit', formData: Activity): void;
}>();

const taskFormRef = ref<FormInstance>();
const durationStr = computed({
  get: () => {
    const totalSeconds = form.durTime || 0;
    const minutes = Math.floor(totalSeconds / 60);
    const seconds = totalSeconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  },
  set: (value: string) => {
    if (value) {
      const [minutes, seconds] = value.split(':').map(Number);
      form.durTime = (minutes * 60 + seconds);
    }
  }
});

// 奖励数据
const rewards = ref<Reward[]>([]);

// 奖励验证规则

const rewardRules: FormRules = {
  name: [
    { required: true, message: '请输入奖励名称', trigger: 'blur' }
  ],
  image: [
    { required: false, message: '请上传奖励图片', trigger: 'change' }
  ],
  rankStart: [
    { required: true, message: '请输入排名区间开始', trigger: 'blur' },
    { type: 'number', min: 0, message: '排名必须大于等于0', trigger: 'blur, change' }
  ],
  rankEnd: [
    { required: true, message: '请输入排名区间结束', trigger: 'blur' },
    { type: 'number', min: 0, message: '排名必须大于等于0', trigger: 'blur, change' },
    {
      validator: (rule, value, callback) => {
        if (!rule.field || !rewards.value || rewards.value.length === 0) {
          callback();
          return;
        }
        const index = parseInt(rule.field.split('.')[1]);
        if (isNaN(index) || index < 0 || index >= rewards.value.length) {
          callback();
          return;
        }
        const reward = rewards.value[index];
        if (!reward || value < reward.rankStart) {
          callback(new Error('排名区间结束必须大于等于开始'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

// 表单数据
const form = reactive<Activity>({
  id: props.initialData?.id || 0,
  activityName: props.initialData?.activityName || '',
  activityType: props.initialData?.activityType || '',
  creatorRole: props.initialData?.creatorRole || '',
  beginTime: props.initialData?.beginTime ? new Date(props.initialData.beginTime) : undefined,
  createTime: props.initialData?.createTime || new Date().toISOString(),
  rewards: rewards.value,
  durTime: props.initialData?.durTime || 60,
  roomPwd: props.initialData?.roomPwd || 0,
  longitude: props.initialData?.longitude || 0,
  latitude: props.initialData?.latitude || 0,
  status: props.initialData?.status || 'not-started'
});

// 初始化奖励数据
const initRewards = () => {
  rewards.value = [];
  if (props.initialData && props.initialData.rewards && props.initialData.rewards.length > 0) {
    props.initialData.rewards.forEach(reward => {
      const fileList = reward.image ? [{
        name: 'reward-image.jpg',
        url: reward.image,
        status: 'success' as UploadStatus,
        uid: Date.now()
      }] : [];
      rewards.value.push({
        id: reward.id || Date.now().toString(),
        name: reward.name || '',
        image: reward.image || '',
        rankStart: reward.rankStart || 1,
        rankEnd: reward.rankEnd || 1,
        fileList,
        uploading: false
      });
    });
  } else {
    rewards.value.push({
      id: Date.now().toString(),
      name: '',
      image: '',
      rankStart: 1,
      rankEnd: 1,
      fileList: [],
      uploading: false
    });
  }
  form.rewards = rewards.value;
};

// 监听rewards变化，同步更新form.rewards
watch(rewards, (newValue) => {
  form.rewards = [...newValue];
}, { deep: true });

// 确保初始值被正确识别
onMounted(() => {
  if (props.initialData) {
    Object.assign(form, props.initialData);
  }
  initRewards();
  // 触发更新
  rewards.value = [...rewards.value];
});

// 处理图片预览
const handlePreview = (uploadFile: UploadFile) => {
  // 实际应用中可能需要打开预览弹窗
};

// 处理图片移除
const handleRemove = (uploadFile: UploadFile, uploadFiles: UploadFile[]) => {
};

// 移除前确认
const beforeRemove = async (uploadFile: UploadFile, uploadFiles: UploadFile[]) => {
  try {
    await ElMessageBox.confirm(`确定移除 ${uploadFile.name}？`);
    return true;
  } catch {
    return false;
  }
};

// 超出限制处理
const handleExceed = (files: File[], uploadFiles: UploadUserFile[]) => {
  ElMessage.warning(`当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，已自动忽略多余文件`);
};

// 文件变更处理 - 仅保存文件引用，不立即上传
const handleFileChange = (file: UploadFile, reward: Reward) => {
  // 保存文件到fileList
  reward.fileList = [file];
  // 设置临时URL用于预览
  reward.image = URL.createObjectURL(file.raw!);
};

// 添加奖励
const addReward = () => {
  rewards.value.push({
    id: Date.now().toString(),
    name: '',
    image: '',
    rankStart: 1,
    rankEnd: 1,
    fileList: [],
    uploading: false
  });
  // 同步更新form.rewards
  form.rewards = [...rewards.value];
};

// 导入apiClient用于表单提交
import apiClient from '@/utils/axiosConfig';

// 删除奖励
const removeReward = (index: number) => {
  if (rewards.value.length > 1) {
    rewards.value.splice(index, 1);
    // 同步更新form.rewards
    form.rewards = [...rewards.value];
  } else {
    ElMessage.warning('至少需要保留一个奖励项');
  }
};

// 处理排名变化
const handleRankChange = (value: number | undefined) => {
};

const rules: FormRules = {
  activityName: [
    { required: true, message: '请输入任务名称', trigger: 'blur' }
  ],
  activityType: [
    { required: true, message: '请输入任务地点', trigger: 'blur' }
  ],
  beginTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  durTime: [
      { required: true, message: '请输入持续时间', trigger: 'blur' },
      { type: 'number', min: 0, max: 60, message: '持续时间必须在0-60秒之间', trigger: 'blur' }
    ],

  roomPwd: [
    { required: true, message: '请输入房间密码', trigger: 'blur' }
  ],
  longitude: [
    { required: true, message: '请输入经度', trigger: 'blur' }
  ],
  latitude: [
    { required: true, message: '请输入纬度', trigger: 'blur' }
  ]
};

const validateAndSubmit = async () => {
  // 首先确保有奖励数据
  if (rewards.value.length === 0) {
    ElMessage.error('请添加至少一个奖励');
    return;
  }

  // 验证所有奖励字段
  let rewardsValid = true;
  rewards.value.forEach((reward, index) => {
    // 验证奖励名称
    if (!reward.name.trim()) {
      ElMessage.error(`第 ${index + 1} 个奖励名称不能为空`);
      rewardsValid = false;
    }

    // 奖励图片非必填
    // if (!reward.fileList || reward.fileList.length === 0) {
    //   ElMessage.error(`第 ${index + 1} 个奖励图片不能为空`);
    //   rewardsValid = false;
    // }

    // 验证排名区间
    if (reward.rankStart === undefined || reward.rankStart < 0) {
      ElMessage.error(`第 ${index + 1} 个奖励排名区间开始必须大于等于0`);
      rewardsValid = false;
    }

    if (reward.rankEnd === undefined || reward.rankEnd < 0) {
      ElMessage.error(`第 ${index + 1} 个奖励排名区间结束必须大于等于0`);
      rewardsValid = false;
    }

    if (reward.rankEnd < reward.rankStart) {
      ElMessage.error(`第 ${index + 1} 个奖励排名区间结束必须大于等于开始`);
      rewardsValid = false;
    }
  });

  if (!rewardsValid) {
    return;
  }

  // 验证表单其他字段
  taskFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        // 创建FormData对象
        const formData = new FormData();

        // 添加基本表单字段
        formData.append('activityName', form.activityName);
        formData.append('activityType', form.activityType);
        formData.append('beginTime', form.beginTime instanceof Date ? form.beginTime.toISOString() : (form.beginTime || ''));
        // 解析durationStr为秒数
        const [minutes, seconds] = durationStr.value.split(':').map(Number);
        formData.append('durTime', (minutes * 60 + seconds).toString());
        formData.append('roomPwd', form.roomPwd.toString());
        formData.append('longitude', form.longitude.toString());
        formData.append('latitude', form.latitude.toString());

        // 添加奖励数据和文件
        rewards.value.forEach((reward, index) => {
          formData.append(`rewards[${index}].name`, reward.name);
          formData.append(`rewards[${index}].rankStart`, reward.rankStart.toString());
          formData.append(`rewards[${index}].rankEnd`, reward.rankEnd.toString());
          // 添加文件
          const file = reward.fileList?.[0]?.raw;
          if (file) {
            formData.append(`rewards[${index}].image`, file);
          }
        });

        // 发送请求
        const response = await apiClient.post('/activity/createActivity', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          onUploadProgress: (progressEvent) => {
            const percentCompleted = Math.round((progressEvent.loaded * 100) / (progressEvent.total || 1));
          }
        });

        ElMessage.success('表单提交成功');
        // 提交成功后可以重置表单或关闭弹窗
        emit('submit', response.data);
      } catch (error) {
          ElMessage.error('表单提交失败，请重试');
        }
    }
  });
};

// 获取当前位置
const getCurrentLocation = () => {
  // 动态加载百度地图API
  const loadBaiduMap = () => {
    return new Promise((resolve, reject) => {
      if (window.BMap) {
        resolve(window.BMap);
      } else {
        const script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = 'https://api.map.baidu.com/api?v=3.0&ak=8WhEvfc2eAm4fRAX44mUZeiDFwMNAYhW&callback=onBMapCallback';
        script.onerror = reject;
        window.onBMapCallback = () => {
          resolve(window.BMap);
        };
        if (document.head) {
          document.head.appendChild(script);
        } else {
          reject(new Error('Document head not found'));
        }
      }
    });
  };

  // 获取位置信息
  const getLocation = () => {
    return new Promise((resolve, reject) => {
      if (!window.BMap) {
        reject(new Error('百度地图API未加载'));
        return;
      }
      const geolocation = new window.BMap.Geolocation();
      geolocation.getCurrentPosition((r) => {
        if (geolocation.getStatus() === window.BMAP_STATUS_SUCCESS) {
          resolve(r);
        } else {
          reject(new Error('获取位置失败'));
        }
      }, { enableHighAccuracy: true });
    });
  };

  // 执行获取位置操作
  loadBaiduMap()
    .then(() => {
      return getLocation();
    })
    .then((r: any) => {
      form.longitude = r.point.lng;
      form.latitude = r.point.lat;
      // 回填任务地点 - 只获取大致位置（省市区）
      let roughAddress = '';
      
      // 尝试从address_detail获取省市区信息
      if (r?.content?.address_detail) {
        const addressDetail = r.content.address_detail;
        const province = typeof addressDetail.province === 'string' ? addressDetail.province : '';
        const city = typeof addressDetail.city === 'string' ? addressDetail.city : '';
        const district = typeof addressDetail.district === 'string' ? addressDetail.district : '';
        
        roughAddress = [province, city, district].filter(Boolean).join(' ');
      }
      // 其次使用基本地址
      else if (r?.content?.address) {
        roughAddress = typeof r.content.address === 'string' ? r.content.address : JSON.stringify(r.content.address);
      }
      else if (r?.address) {
        if (typeof r.address === 'string') {
          roughAddress = r.address;
        } else if (typeof r.address === 'object' && r.address !== null) {
          // 按照district+street+street_number格式构建地址
          const district = typeof r.address.district === 'string' ? r.address.district : '';
          const street = typeof r.address.street === 'string' ? r.address.street : '';
          const streetNumber = typeof r.address.street_number === 'string' ? r.address.street_number : '';
          
          roughAddress = [district, street, streetNumber].filter(Boolean).join('');
        } else {
          roughAddress = JSON.stringify(r.address);
        }

      }
      else {
          // 所有地址来源都为空时
          roughAddress = '';
          ElMessage.warning('未获取到有效地址信息');
        }
      
      // 回填任务地点
      if (roughAddress) {
        form.activityType = roughAddress;
      }
    })
    .catch(error => {
      ElMessage.error('获取位置信息失败');
    });

  
};

// 暴露验证提交方法给父组件
defineExpose({
  validateAndSubmit
});
</script>

<style scoped>
.reward-container {
  margin-top: 10px;
}

.reward-item {
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.reward-form-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  width: 100%;
  margin: 0 -10px;
}

.delete-btn-col {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 10px;
}

/* 调整输入框大小 */
.el-input, .el-input-number {
  width: 100%;
}

/* 按钮样式 */
.el-button + .el-button {
  margin-left: 5px;
}
</style>