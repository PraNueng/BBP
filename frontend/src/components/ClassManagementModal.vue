<template>
  <div
    v-if="show"
    class="fixed inset-0 backdrop-blur bg-black/20 flex items-center justify-center p-4 z-50 overflow-y-auto"
    @click.self="$emit('close')"
  >
    <div class="bg-white rounded-2xl shadow-2xl max-w-5xl w-full max-h-[90vh] overflow-hidden flex flex-col my-8">
      <!-- Header -->
      <div class="bg-gradient-to-r from-indigo-800 to-blue-700 p-6 flex items-center justify-between">
        <div>
          <h2 class="text-2xl font-bold text-white">จัดการคลาสของครู</h2>
          <p v-if="tutor" class="text-purple-100 mt-1">{{ tutor.nickname || tutor.username }}</p>
          <p v-else class="text-purple-100 mt-1">เพิ่มหรือลบครูในคลาส</p>
        </div>
        <button
          @click="$emit('close')"
          class="p-2 hover:bg-white/20 rounded-lg transition"
        >
          <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- Tabs -->
      <div class="border-b border-gray-200 bg-gray-50 px-6 pt-4">
        <div class="flex gap-2 overflow-x-auto pb-2">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            @click="changeTab(tab.id)"
            :class="[
              'px-6 py-2 rounded-t-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === tab.id
                ? 'bg-white text-purple-600 border-t-2 border-purple-600'
                : 'text-gray-600 hover:text-purple-600 hover:bg-white/50'
            ]"
          >
            {{ tab.label }}
            <span
              v-if="getTabCount(tab.id) > 0"
              class="px-2 py-0.5 bg-purple-100 text-purple-700 rounded-full text-xs font-bold"
            >
              {{ getTabCount(tab.id) }}
            </span>
          </button>
        </div>
      </div>

      <!-- Content -->
      <div class="flex-1 overflow-y-auto p-6">
        <!-- Success/Error Messages -->
        <div v-if="successMessage" class="mb-4 p-4 bg-green-50 border-l-4 border-green-500 text-green-800 rounded-lg flex items-center gap-2">
          <svg class="h-5 w-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span class="font-semibold">{{ successMessage }}</span>
        </div>
        <div v-if="errorMessage" class="mb-4 p-4 bg-red-50 border-l-4 border-red-500 text-red-800 rounded-lg flex items-center gap-2">
          <svg class="h-5 w-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span class="font-semibold">{{ errorMessage }}</span>
        </div>

        <!-- Monthly Tab -->
        <div v-show="activeTab === 'monthly'">
          <!-- Filter Bar -->
          <FilterClassManagementBar
            :subjects="subjects"
            :grades="grades"
            :subtypes="monthlySubtypes"
            class-type="monthly"
            :show-status-filter="true"
            @filter="handleMonthlyFilter"
          />

          <MonthlyClassList
            :tutor-id="tutor?.id || null"
            :filters="monthlyFilters"
            :refresh-trigger="refreshTrigger"
            @success="handleSuccess"
            @error="handleError"
            @count-update="updateMonthlyCount"
          />

          <!-- Pagination -->
          <Pagination
            v-if="monthlyTotalPages > 1"
            v-model="monthlyCurrentPage"
            :total-pages="monthlyTotalPages"
            @update:modelValue="handleMonthlyPageChange"
          />
        </div>

        <!-- Hourly Group Tab -->
        <div v-show="activeTab === 'hourly-group'">
          <!-- Filter Bar -->
          <FilterClassManagementBar
            :subjects="subjects"
            :grades="grades"
            :subtypes="hourlyGroupSubtypes"
            class-type="hourly-group"
            :show-status-filter="true"
            @filter="handleHourlyGroupFilter"
          />

          <HourlyGroupClassList
            :tutor-id="tutor?.id || null"
            :filters="hourlyGroupFilters"
            :refresh-trigger="refreshTrigger"
            @success="handleSuccess"
            @error="handleError"
            @count-update="updateHourlyGroupCount"
          />

          <!-- Pagination -->
          <Pagination
            v-if="hourlyGroupTotalPages > 1"
            v-model="hourlyGroupCurrentPage"
            :total-pages="hourlyGroupTotalPages"
            @update:modelValue="handleHourlyGroupPageChange"
          />
        </div>

        <!-- Hourly Individual Tab -->
        <div v-show="activeTab === 'hourly-individual'">
          <!-- Filter Bar -->
          <FilterClassManagementBar
            :subjects="subjects"
            :grades="grades"
            :subtypes="[]"
            class-type="hourly-individual"
            :show-status-filter="true"
            @filter="handleHourlyIndividualFilter"
          />

          <!-- Search for PV Classes -->
          <div class="mb-4">
            <input
              v-model="classSearchQuery"
              @input="handleClassSearch"
              type="text"
              placeholder="ค้นหาชื่อคลาส PV"
              class="w-full px-4 py-3 border-2 border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all shadow-sm"
            />
          </div>

          <HourlyIndividualClassList
            :tutor-id="tutor?.id || null"
            :filters="hourlyIndividualFilters"
            :refresh-trigger="refreshTrigger"
            @success="handleSuccess"
            @error="handleError"
            @count-update="updateHourlyIndividualCount"
          />

          <!-- Pagination -->
          <Pagination
            v-if="hourlyIndividualTotalPages > 1"
            v-model="hourlyIndividualCurrentPage"
            :total-pages="hourlyIndividualTotalPages"
            @update:modelValue="handleHourlyIndividualPageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed, defineAsyncComponent } from 'vue';
import api from '@/api.js';
import FilterClassManagementBar from '@/components/FilterClassManagementBar.vue';
import Pagination from '@/components/Pagination.vue';

const MonthlyClassList = defineAsyncComponent(() => import('../views/MonthlyClassList.vue'));
const HourlyGroupClassList = defineAsyncComponent(() => import('../views/HourlyGroupClassList.vue'));
const HourlyIndividualClassList = defineAsyncComponent(() => import('../views/HourlyIndividualClassList.vue'));

const props = defineProps({
  show: {
    type: Boolean,
    required: true
  },
  tutor: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['close', 'updated']);

const activeTab = ref('monthly');
const subjects = ref([]);
const grades = ref([]);
const monthlySubtypes = ref([]);
const hourlyGroupSubtypes = ref([]);
const successMessage = ref('');
const errorMessage = ref('');
const refreshTrigger = ref(0);

// Pagination states
const ITEMS_PER_PAGE = 5;

// Monthly
const monthlyCurrentPage = ref(1);
const monthlyTotalCount = ref(0);
const monthlyFilters = ref({});
const monthlyTotalPages = computed(() => Math.ceil(monthlyTotalCount.value / ITEMS_PER_PAGE));

// Hourly Group
const hourlyGroupCurrentPage = ref(1);
const hourlyGroupTotalCount = ref(0);
const hourlyGroupFilters = ref({});
const hourlyGroupTotalPages = computed(() => Math.ceil(hourlyGroupTotalCount.value / ITEMS_PER_PAGE));

// Hourly Individual
const hourlyIndividualCurrentPage = ref(1);
const hourlyIndividualTotalCount = ref(0);
const hourlyIndividualFilters = ref({});
const classSearchQuery = ref('');
const hourlyIndividualTotalPages = computed(() => Math.ceil(hourlyIndividualTotalCount.value / ITEMS_PER_PAGE));

const shouldShowHourlyIndividualContent = computed(() => {
  if (activeTab.value !== 'hourly-individual') return true;
  if (!classSearchQuery.value.trim()) return true;
  
  // จะ filter ใน HourlyIndividualClassList component แทน
  return true;
});

const tabs = [
  { id: 'monthly', label: 'รายเดือน' },
  { id: 'hourly-group', label: 'กลุ่มรวม' },
  { id: 'hourly-individual', label: 'PV-เดี่ยว/PV-กลุ่ม' }
];

const getTabCount = (tabId) => {
  switch (tabId) {
    case 'monthly':
      return monthlyTotalCount.value;
    case 'hourly-group':
      return hourlyGroupTotalCount.value;
    case 'hourly-individual':
      return hourlyIndividualTotalCount.value;
    default:
      return 0;
  }
};

const changeTab = (tabId) => {
  activeTab.value = tabId;
  classSearchQuery.value = '';
  // Reset pagination when changing tabs
  if (tabId === 'monthly') monthlyCurrentPage.value = 1;
  if (tabId === 'hourly-group') hourlyGroupCurrentPage.value = 1;
  if (tabId === 'hourly-individual') hourlyIndividualCurrentPage.value = 1;
};

// Filter handlers
const handleMonthlyFilter = (filters) => {
  monthlyFilters.value = { ...filters, page: 1, limit: ITEMS_PER_PAGE };
  monthlyCurrentPage.value = 1;
  refreshTrigger.value++;
};

const handleHourlyGroupFilter = (filters) => {
  hourlyGroupFilters.value = { ...filters, page: 1, limit: ITEMS_PER_PAGE };
  hourlyGroupCurrentPage.value = 1;
  refreshTrigger.value++;
};

const handleHourlyIndividualFilter = (filters) => {
  hourlyIndividualFilters.value = { 
    ...filters, 
    className: classSearchQuery.value,
    page: 1, 
    limit: ITEMS_PER_PAGE 
  };
  hourlyIndividualCurrentPage.value = 1;
  refreshTrigger.value++;
};

const handleClassSearch = () => {
  // ใช้ filters ปัจจุบัน แต่เพิ่ม className และ reset page
  hourlyIndividualFilters.value = { 
    ...hourlyIndividualFilters.value,
    className: classSearchQuery.value,
    page: 1, 
    limit: ITEMS_PER_PAGE 
  };
  hourlyIndividualCurrentPage.value = 1;
  refreshTrigger.value++;
};

// Pagination handlers
const handleMonthlyPageChange = (page) => {
  monthlyFilters.value = { ...monthlyFilters.value, page, limit: ITEMS_PER_PAGE };
  refreshTrigger.value++;
};

const handleHourlyGroupPageChange = (page) => {
  hourlyGroupFilters.value = { ...hourlyGroupFilters.value, page, limit: ITEMS_PER_PAGE };
  refreshTrigger.value++;
};

const handleHourlyIndividualPageChange = (page) => {
  hourlyIndividualFilters.value = { ...hourlyIndividualFilters.value, page, limit: ITEMS_PER_PAGE };
  refreshTrigger.value++;
};

// Count update handlers (from child components)
const updateMonthlyCount = (count) => {
  monthlyTotalCount.value = count;
};

const updateHourlyGroupCount = (count) => {
  hourlyGroupTotalCount.value = count;
};

const updateHourlyIndividualCount = (count) => {
  hourlyIndividualTotalCount.value = count;
};

const loadData = async () => {
  try {
    const [subjectsRes, gradesRes, monthlyRes, hourlyRes] = await Promise.all([
      api.get('/subjects', { params: { active: true } }),
      api.get('/grades'),
      api.get('/monthly-subtypes', { params: { active: true } }),
      api.get('/hourly-group-subtypes', { params: { active: true } })
    ]);

    subjects.value = subjectsRes.data;
    grades.value = gradesRes.data;
    monthlySubtypes.value = monthlyRes.data;
    hourlyGroupSubtypes.value = hourlyRes.data;
  } catch (error) {
    console.error('Error loading data:', error);
    handleError('ไม่สามารถโหลดข้อมูลได้');
  }
};

const handleSuccess = (message) => {
  successMessage.value = message;
  errorMessage.value = '';
  refreshTrigger.value++;
  emit('updated');
  setTimeout(() => {
    successMessage.value = '';
  }, 3000);
};

const handleError = (message) => {
  errorMessage.value = message;
  successMessage.value = '';
  setTimeout(() => {
    errorMessage.value = '';
  }, 5000);
};

watch(() => props.show, (newVal) => {
  if (newVal) {
    loadData();
    activeTab.value = 'monthly';
    classSearchQuery.value = '';
    // Reset filters and pagination
    monthlyFilters.value = { page: 1, limit: ITEMS_PER_PAGE };
    hourlyGroupFilters.value = { page: 1, limit: ITEMS_PER_PAGE };
    hourlyIndividualFilters.value = { page: 1, limit: ITEMS_PER_PAGE };
    monthlyCurrentPage.value = 1;
    hourlyGroupCurrentPage.value = 1;
    hourlyIndividualCurrentPage.value = 1;
  }
});

onMounted(() => {
  if (props.show) {
    loadData();
    monthlyFilters.value = { page: 1, limit: ITEMS_PER_PAGE };
    hourlyGroupFilters.value = { page: 1, limit: ITEMS_PER_PAGE };
    hourlyIndividualFilters.value = { page: 1, limit: ITEMS_PER_PAGE };
  }
});
</script>