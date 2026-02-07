<template>
  <NavBar/>
  <div class="min-h-screen bg-gradient-to-br from-slate-900 via-amber-900 to-yellow-900 p-4">
    <BackButton to="/class-management-total" class="pb-10 pl-2" />
    <!-- Header -->
    <div class="max-w-7xl mx-auto">
      <div class="bg-gradient-to-r from-yellow-800 to-amber-500 rounded-t-3xl shadow-2xl p-8">
        <h1 class="text-5xl font-extrabold text-white text-center drop-shadow-lg">
          จัดการข้อมูลคลาสทั้งหมด
        </h1>
        <!-- <p class="text-purple-50 text-center mt-3 text-lg">
          ดูและจัดการข้อมูลคลาสทั้งหมด
        </p> -->
      </div>

      <div class="bg-white rounded-b-3xl shadow-2xl p-8">
        <!-- Tabs -->
        <div class="flex gap-4 mb-8 overflow-x-auto pb-2">
          <button
            @click="activeTab = 'monthly'"
            :class="[
              'px-6 py-3 rounded-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === 'monthly'
                ? 'bg-gradient-to-r from-pink-500 to-pink-600 text-white shadow-lg scale-105'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            <Calendar class="w-5 h-5" />
            รายเดือน
          </button>
          <button
            @click="activeTab = 'hourly-group'"
            :class="[
              'px-6 py-3 rounded-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === 'hourly-group'
                ? 'bg-gradient-to-r from-orange-500 to-orange-600 text-white shadow-lg scale-105'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            <Users class="w-5 h-5" />
            กลุ่มรวม
          </button>
          <button
            @click="activeTab = 'hourly-individual'"
            :class="[
              'px-6 py-3 rounded-lg font-semibold transition-all whitespace-nowrap flex items-center gap-2',
              activeTab === 'hourly-individual'
                ? 'bg-gradient-to-r from-blue-500 to-blue-600 text-white shadow-lg scale-105'
                : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
            ]"
          >
            <User class="w-5 h-5" />
            PV-เดี่ยว / PV-กลุ่ม
          </button>
        </div>

        <!-- Messages (moved to fixed corner notifications) -->

        <FilterAddClass
          :subjects="subjects"
          :grades="grades"
          @filter="handleFilter"
        />

        <!-- Add Button -->
        <div class="mb-6">
          <button
            @click="openCreateModal"
            class="px-6 py-3 bg-gradient-to-r from-green-500 to-green-600 text-white font-bold rounded-lg hover:from-green-600 hover:to-green-700 transition-all shadow-lg flex items-center gap-2"
          >
            <Plus class="w-5 h-5" />
            สร้างคลาส {{ getTabLabel() }}
          </button>
        </div>

        <!-- Classes Table -->
        <div v-if="isLoading" class="text-center py-12">
          <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          <p class="mt-4 text-gray-600">กำลังโหลดข้อมูล...</p>
        </div>
        <div v-else-if="getCurrentClasses().length === 0" class="text-center py-12">
            <p class="text-gray-500 text-lg">
              {{ filters.subjectId || filters.gradeId || filters.keyword 
                ? 'ไม่พบคลาสที่ตรงกับเงื่อนไขที่เลือก' 
                : 'ยังไม่มีคลาสเรียน' }}
            </p>
        </div>
        <div v-else class="overflow-x-auto rounded-xl shadow-lg">
          <table class="w-full">
            <thead class="bg-gradient-to-r from-amber-800 via-amber-700 to-yellow-700">
              <tr class="text-center">
                <th class="px-6 py-4 text-sm font-bold text-white">ชื่อคลาส</th>
                <th class="px-6 py-4 text-sm font-bold text-white">วิชา</th>
                <th class="px-6 py-4 text-sm font-bold text-white" v-if="activeTab !== 'hourly-individual'">ประเภท</th>
                <th class="px-6 py-4 text-sm font-bold text-white">ชั้นเรียน</th>
                <th class="px-6 py-4 text-sm font-bold text-white">ครูผู้สอน</th>
                <th class="px-6 py-4 text-sm font-bold text-white">นักเรียน</th>
                <th class="px-6 py-4 text-sm font-bold text-white">สถานะ</th>
                <th class="px-6 py-4 text-sm font-bold text-white">จัดการ</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100 bg-white">
              <tr
                v-for="cls in filteredClasses"
                :key="cls.id"
                class="hover:bg-gradient-to-r hover:from-blue-50 hover:to-indigo-50 transition-all text-center"
              >
                <td class="px-6 py-4 text-sm font-semibold text-gray-800">
                  {{ cls.className }}
                </td>
                <td class="px-6 py-4 text-sm text-gray-700">
                  {{ cls.subjectName }}
                </td>
                <td class="px-6 py-4 text-sm text-gray-700" v-if="activeTab !== 'hourly-individual'">
                  <span class="px-3 py-1 bg-purple-100 text-purple-700 rounded-full font-medium text-xs">
                    {{ cls.subtypeName }}
                  </span>
                </td>
                <td class="px-6 py-4 text-sm text-gray-700">
                  {{ cls.gradeName }}
                </td>
                
                <!-- แสดงครู -->
                <td class="px-6 py-4 text-sm">
                  <!-- Monthly & Group: แสดงครูหลายคน -->
                  <div v-if="activeTab === 'monthly' || activeTab === 'hourly-group' || activeTab === 'hourly-individual'">
                    <div v-if="cls.tutors && cls.tutors.length > 0" class="flex flex-wrap gap-1 justify-center">
                      <span
                        v-for="tutor in cls.tutors.slice(0, 2)"
                        :key="tutor.id"
                        class="px-2 py-1 bg-blue-100 text-blue-700 rounded-full font-medium text-xs"
                      >
                        {{ tutor.nickname }}
                      </span>
                      <span
                        v-if="cls.tutors.length > 2"
                        class="px-2 py-1 bg-gray-100 text-gray-600 rounded-full font-medium text-xs"
                      >
                        +{{ cls.tutors.length - 2 }}
                      </span>
                    </div>
                    <span v-else class="text-gray-400 italic">ยังไม่มีครู</span>
                  </div>
                  
                  <!-- Individual: แสดงครูคนเดียว -->
                  <!-- <div v-else>
                    <span
                      v-if="cls.tutorName"
                      class="px-3 py-1 bg-blue-100 text-blue-700 rounded-full font-medium text-xs"
                    >
                      {{ cls.tutorName }}
                    </span>
                    <span v-else class="text-gray-400 italic">ยังไม่มีครู</span>
                  </div> -->
                </td>
                
                <td class="px-6 py-4 text-sm">
                  <span class="px-3 py-1 bg-green-100 text-green-700 rounded-full font-medium text-xs">
                    {{ cls.totalStudents || 0 }} คน
                  </span>
                </td>
                <td class="px-6 py-4 text-sm">
                  <span
                    v-if="cls.isActive"
                    class="text-green-600 font-medium flex items-center justify-center gap-1"
                  >
                    <CheckCircle class="w-4 h-4" />
                    เปิดใช้งาน
                  </span>
                  <span
                    v-else
                    class="text-red-600 font-medium flex items-center justify-center gap-1"
                  >
                    <XCircle class="w-4 h-4" />
                    ปิดใช้งาน
                  </span>
                </td>
                <td class="px-6 py-4">
                  <div class="flex justify-center gap-2">
                    <button
                      @click="viewClassDetail(cls)"
                      class="px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 text-sm font-semibold shadow-md hover:shadow-lg transition-all transform hover:scale-105 flex items-center gap-1"
                    >
                      <Eye class="w-4 h-4" />
                      ข้อมูล
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Create Modal -->
    <ModalShell v-if="showCreateModal" @close="closeCreateModal" :showBackground="true" wrapperClass="bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 w-full max-w-2xl max-h-[90vh] overflow-hidden relative rounded-3xl shadow-2xl">
      <template #icon>
        <div class="p-2 bg-white/20 rounded-lg">
          <Plus class="w-6 h-6 text-white" />
        </div>
      </template>
      <template #title>สร้างคลาส{{ getTabLabel() }}ใหม่</template>
      <template #closeIcon>
        <X class="w-6 h-6 group-hover:rotate-90 transition-transform" />
      </template>
      <div class="relative z-10 max-h-[calc(90vh-180px)] p-6">
          <div class="space-y-4">
            <div>
              <label class="block text-white font-semibold mb-2">
                วิชา <span class="text-red-400">*</span>
              </label>
              <select
                v-model="newClass.subjectId"
                :class="[
                  'w-full px-4 py-3 border rounded-lg focus:ring-2 bg-white text-gray-800',
                  !newClass.subjectId && errorMessage ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-green-500'
                ]"
              >
                <option value="">-- เลือกวิชา --</option>
                <option v-for="subject in subjects" :key="subject.id" :value="subject.id">
                  {{ subject.subjectName }}
                </option>
              </select>
              <p v-if="!newClass.subjectId && errorMessage === 'กรุณาเลือกวิชา'" class="mt-1 text-red-300 text-sm">
                กรุณาเลือกวิชา
              </p>
            </div>

            <!-- แสดงฟิลด์ชั้นเรียน เฉพาะคลาสรายเดือนและกลุ่ม -->
            <div v-if="activeTab !== 'hourly-individual'">
              <label class="block text-white font-semibold mb-2">
                ชั้นเรียน <span class="text-red-400">*</span>
              </label>
              <select
                v-model="newClass.gradeId"
                :class="[
                  'w-full px-4 py-3 border rounded-lg focus:ring-2 bg-white text-gray-800',
                  !newClass.gradeId && errorMessage ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-green-500'
                ]"
              >
                <option value="">-- เลือกชั้นเรียน --</option>
                <option v-for="grade in grades" :key="grade.id" :value="grade.id">
                  {{ grade.gradeName }}
                </option>
              </select>
              <p v-if="!newClass.gradeId && errorMessage === 'กรุณาเลือกชั้นเรียน'" class="mt-1 text-red-300 text-sm">
                กรุณาเลือกชั้นเรียน
              </p>
            </div>

            <!-- แสดง subtype เฉพาะคลาสรายเดือนและกลุ่ม -->
            <div v-if="activeTab !== 'hourly-individual'">
              <label class="block text-white font-semibold mb-2">
                ประเภทคลาส <span class="text-red-400">*</span>
              </label>
              <select
                v-model="newClass.subtypeId"
                :class="[
                  'w-full px-4 py-3 border rounded-lg focus:ring-2 bg-white text-gray-800',
                  !newClass.subtypeId && errorMessage ? 'border-red-500 focus:ring-red-500' : 'border-gray-300 focus:ring-green-500'
                ]"
              >
                <option value="">-- เลือกประเภท --</option>
                <option
                  v-for="subtype in getCurrentSubtypes()"
                  :key="subtype.id"
                  :value="subtype.id"
                >
                  {{ subtype.subtypeName }}
                </option>
              </select>
              <p v-if="!newClass.subtypeId && errorMessage === 'กรุณาเลือกประเภทคลาส'" class="mt-1 text-red-300 text-sm">
                กรุณาเลือกประเภทคลาส
              </p>
            </div>

            <!-- Autocomplete สำหรับนักเรียน (เฉพาะคลาสเดี่ยว) -->
            <div v-if="activeTab === 'hourly-individual'" class="student-autocomplete">
              <label class="block text-white font-semibold mb-2">
                นักเรียน <span class="text-red-400">*</span>
                <span class="text-white/70 text-sm ml-2">(สามารถเลือกได้หลายคน)</span>
              </label>
              <div class="relative">
                <input
                  v-model="studentSearchQuery"
                  @input="handleStudentSearch"
                  @focus="handleStudentFocus"
                  type="text"
                  placeholder="พิมพ์ชื่อหรือชื่อเล่นนักเรียน..."
                  class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 bg-white text-gray-800"
                  autocomplete="off"
                />
                
                <!-- Dropdown รายการนักเรียน -->
                <div
                  v-if="showStudentDropdown && filteredStudents.length > 0"
                  class="absolute z-50 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-xl max-h-60 overflow-y-auto"
                >
                  <button
                    v-for="student in filteredStudents"
                    :key="student.id"
                    @click="selectStudent(student)"
                    type="button"
                    :disabled="isStudentAlreadySelected(student.id)"
                    :class="[
                      'w-full px-4 py-3 text-left hover:bg-blue-50 transition-colors border-b border-gray-100 last:border-0',
                      isStudentAlreadySelected(student.id) ? 'opacity-50 cursor-not-allowed bg-gray-50' : ''
                    ]"
                  >
                    <div class="font-semibold text-gray-800">
                      {{ student.nickname }}
                      <span v-if="isStudentAlreadySelected(student.id)" class="text-xs text-green-600 ml-2">✓ เลือกแล้ว</span>
                    </div>
                    <div class="text-sm text-gray-600">
                      {{ student.firstName }} {{ student.lastName }}
                      <span v-if="student.studentCode" class="text-gray-400 ml-2">
                        ({{ student.studentCode }})
                      </span>
                    </div>
                  </button>
                </div>
                
                <!-- ไม่พบนักเรียน -->
                <div
                  v-if="showStudentDropdown && studentSearchQuery && filteredStudents.length === 0"
                  class="absolute z-50 w-full mt-1 bg-white border border-gray-300 rounded-lg shadow-xl p-4 text-center text-gray-500"
                >
                  ไม่พบนักเรียนที่ค้นหา
                </div>
              </div>
              
              <!-- แสดงนักเรียนที่เลือกทั้งหมด (หลายคน) -->
              <div v-if="selectedStudents.length > 0" class="mt-3 space-y-2">
                <div
                  v-for="student in selectedStudents"
                  :key="student.id"
                  class="p-3 bg-white/20 rounded-lg flex items-center justify-between"
                >
                  <div>
                    <span class="text-white font-semibold">{{ student.nickname }}</span>
                    <span class="text-white/80 text-sm ml-2">
                      ({{ student.firstName }} {{ student.lastName }})
                    </span>
                  </div>
                  <button
                    @click="removeSelectedStudent(student.id)"
                    type="button"
                    class="text-white/80 hover:text-white transition"
                  >
                    <X class="w-5 h-5" />
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="flex gap-4 justify-end pt-6">
            <button
              @click="closeCreateModal"
              type="button"
              class="px-6 py-3 bg-slate-700 text-white font-semibold rounded-xl hover:bg-slate-600 transition-all shadow-lg"
            >
              ยกเลิก
            </button>
            <button
              @click="handleCreateClass"
              :disabled="isSubmitting"
              class="px-6 py-3 font-semibold rounded-xl text-white transition-all shadow-lg transform hover:scale-105 disabled:transform-none disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2 bg-gradient-to-r from-green-600 to-green-700 hover:from-green-700 hover:to-emerald-800"
            >
            <div
                v-if="isSubmitting"
                class="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"
            >
            </div>
              {{ isSubmitting ? 'กำลังสร้าง...' : 'ตกลง' }}
            </button>
          </div>
      </div>
      <template #footer>
        <!-- empty - header actions already provided in default layout -->
      </template>
    </ModalShell>

    <!-- Detail Modal -->
    <ModalShell v-if="showDetailModal && selectedClass" @close="closeDetailModal" :showBackground="true" wrapperClass="bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 w-full max-w-4xl max-h-[90vh] overflow-hidden relative rounded-3xl shadow-2xl">
      <template #icon>
        <div class="p-2 bg-white/20 rounded-lg">
          <Eye class="w-6 h-6 text-white" />
        </div>
      </template>
      <template #title>รายละเอียดคลาส</template>
      <template #closeIcon>
        <X class="w-6 h-6 group-hover:rotate-90 transition-transform" />
      </template>
      <div class="relative z-10 max-h-[calc(90vh-180px)] p-6">
          <div class="space-y-6">
            <!-- Class Info -->
            <div class="bg-white/10 rounded-2xl p-6 backdrop-blur-sm">
              <h3 class="text-xl font-bold text-white mb-4 flex items-center gap-2">
                <BookOpen class="w-5 h-5" />
                ข้อมูลคลาส
              </h3>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-white">
                <div>
                  <p class="text-gray-300 text-sm">ชื่อคลาส</p>
                  <p class="font-semibold text-lg">{{ selectedClass.className }}</p>
                </div>
                <div>
                  <p class="text-gray-300 text-sm">วิชา</p>
                  <p class="font-semibold text-lg">{{ selectedClass.subjectName }}</p>
                </div>
                <div v-if="activeTab !== 'hourly-individual'">
                  <p class="text-gray-300 text-sm">ประเภท</p>
                  <p class="font-semibold text-lg">{{ selectedClass.subtypeName }}</p>
                </div>
                <div>
                  <p class="text-gray-300 text-sm">ชั้นเรียน</p>
                  <p class="font-semibold text-lg">{{ selectedClass.gradeName }}</p>
                </div>
                
                <!-- แสดงครูตามประเภทคลาส -->
                <div>
                  <p class="text-gray-300 text-sm">ครูผู้สอน</p>
                  <div v-if="(activeTab === 'monthly' || activeTab === 'hourly-group' || activeTab === 'hourly-individual') && selectedClass.tutors && selectedClass.tutors.length > 0">
                    <p class="font-semibold text-lg">{{ selectedClass.tutors.map(t => t.nickname).join(', ') }}</p>
                  </div>
                  <p v-else-if="selectedClass.tutorName" class="font-semibold text-lg">{{ selectedClass.tutorName }}</p>
                  <p v-else class="font-semibold text-lg text-gray-400">ยังไม่มีครู</p>
                </div>
                
                <div>
                  <p class="text-gray-300 text-sm">สถานะ</p>
                  <p
                    :class="[
                      'font-semibold text-lg',
                      selectedClass.isActive ? 'text-green-400' : 'text-red-400'
                    ]"
                  >
                    {{ selectedClass.isActive ? '✓ เปิดใช้งาน' : '✗ ปิดใช้งาน' }}
                  </p>
                </div>
                <div v-if="activeTab === 'hourly-individual' && selectedClass.studentName">
                  <p class="text-gray-300 text-sm">นักเรียน</p>
                  <p class="font-semibold text-lg">{{ selectedClass.studentName }}</p>
                </div>
              </div>
            </div>

            <!-- Students List (for group classes) -->
            <div
              v-if="activeTab === 'monthly' || activeTab === 'hourly-group' || activeTab === 'hourly-individual'"
              class="bg-white/10 rounded-2xl p-6 backdrop-blur-sm"
            >
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-xl font-bold text-white flex items-center gap-2">
                <Users class="w-5 h-5" />
                รายชื่อนักเรียน ({{ classStudents.length }} คน)
              </h3>
              <button
                v-if="activeTab !== 'hourly-individual' || !selectedClass.className.startsWith('PV-เดี่ยว')"
                @click="openManageStudentsModal"
                class="px-4 py-2 bg-gradient-to-r from-green-500 to-emerald-600 text-white rounded-lg hover:from-green-600 hover:to-emerald-700 transition-all shadow-md flex items-center gap-2 text-sm font-semibold"
              >
                เพิ่ม/ลบ นักเรียน
              </button>
            </div>
              
              <!-- แสดงรายชื่อนักเรียน -->
              <div v-if="classStudents.length > 0" class="space-y-2">
                <div
                  v-for="(student, index) in classStudents"
                  :key="student.id"
                  class="bg-white/5 rounded-lg p-4 flex items-center justify-between hover:bg-white/10 transition-colors"
                >
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-full flex items-center justify-center text-white font-bold">
                      {{ index + 1 }}
                    </div>
                    <div>
                      <p class="text-white font-semibold">{{ student.nickname || `${student.firstName} ${student.lastName}` }}</p>
                      <p class="text-gray-300 text-sm">{{ student.firstName }} {{ student.lastName }}</p>
                      <p v-if="student.studentCode" class="text-gray-400 text-xs">รหัส: {{ student.studentCode }}</p>
                    </div>
                  </div>
                  
                  <!-- แสดงสถานะเพิ่มเติม (ถ้ามี) -->
                  <div v-if="student.status" class="text-sm">
                    <span
                      :class="[
                        'px-3 py-1 rounded-full font-medium',
                        student.status === 'active' ? 'bg-green-500/20 text-green-300' : 'bg-gray-500/20 text-gray-300'
                      ]"
                    >
                      {{ student.status === 'active' ? 'กำลังเรียน' : 'หยุดเรียน' }}
                    </span>
                  </div>
                </div>
              </div>
              
              <!-- กรณีไม่มีนักเรียน -->
              <div v-else class="bg-white/5 rounded-lg p-4">
                <p class="text-gray-300 text-center">
                  ยังไม่มีนักเรียนในคลาสนี้
                </p>
              </div>
            </div>

            <!-- Statistics -->
            <div class="bg-white/10 rounded-2xl p-6 backdrop-blur-sm">
              <h3 class="text-xl font-bold text-white mb-4 flex items-center gap-2">
                <Clock class="w-5 h-5" />
                สถิติ
              </h3>
              <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
                <div class="bg-white/5 rounded-lg p-4">
                  <p class="text-gray-300 text-sm">จำนวนนักเรียน</p>
                  <p class="text-2xl font-bold text-white">{{ selectedClass.totalStudents || 0 }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="flex gap-4 justify-end pt-6">
            <!-- ปุ่มเลื่อนชั้น (แสดงเฉพาะรายเดือนและกลุ่มรวม และไม่ใช่ชั้นสูงสุด) -->
            <button
              v-if="(activeTab === 'monthly' || activeTab === 'hourly-group') && !isMaxGrade && selectedClass.gradeName !== 'อื่น ๆ'"
              @click="handlePromoteStudents"
              class="px-6 py-3 font-semibold rounded-xl text-white transition-all shadow-lg transform hover:scale-105 flex items-center gap-2 bg-gradient-to-r from-green-600 to-emerald-700 hover:from-green-700 hover:to-emerald-800"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 11l5-5m0 0l5 5m-5-5v12"/>
              </svg>
              {{ promoteButtonText }}
            </button>
            
            <!-- ปุ่ม inactive ทั้งหมด (แสดงเฉพาะชั้นสูงสุด) -->
            <button
              v-if="(activeTab === 'monthly' || activeTab === 'hourly-group') && isMaxGrade && selectedClass.gradeName !== 'อื่น ๆ'"
              @click="handleDeactivateAllStudents"
              class="px-6 py-3 font-semibold rounded-xl text-white transition-all shadow-lg transform hover:scale-105 flex items-center gap-2 bg-gradient-to-r from-red-600 to-pink-700 hover:from-red-700 hover:to-pink-800"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"/>
              </svg>
              Inactive คลาสนี้ให้นักเรียนทั้งหมด
            </button>
          </div>
      </div>
      <template #footer>
        <div class="p-6 border-t border-white/5 bg-gradient-to-t from-transparent to-black/10 flex gap-4 justify-end">
          <button
            @click="closeDetailModal"
            class="px-6 py-3 bg-slate-700 text-white font-semibold rounded-xl hover:bg-slate-600 transition-all shadow-lg"
          >
            ปิด
          </button>
        </div>
      </template>
    </ModalShell>

    <!-- Manage Students Modal -->
    <ModalShell v-if="showManageStudentsModal && selectedClass" @close="closeManageStudentsModal" :showBackground="true" wrapperClass="bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 w-full max-w-5xl max-h-[90vh] overflow-hidden relative rounded-3xl shadow-2xl">
      <template #icon>
        <div class="p-2 bg-white/20 rounded-lg">
          <Users class="w-6 h-6 text-white" />
        </div>
      </template>
      <template #title>
        <div>
          <div class="text-2xl font-bold text-white">เพิ่ม/ลบ นักเรียน</div>
          <div class="text-purple-100 text-sm">คลาส {{ selectedClass.className }}</div>
        </div>
      </template>
      <template #closeIcon>
        <X class="w-6 h-6 group-hover:rotate-90 transition-transform" />
      </template>
      <div class="relative z-10 max-h-[calc(90vh-180px)] p-6">
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <!-- Left: Add Students -->
            <div class="bg-white/10 rounded-2xl p-6 backdrop-blur-sm">
              <h3 class="text-xl font-bold text-white mb-4 flex items-center gap-2">
                <Plus class="w-5 h-5" />
                เพิ่มนักเรียน
              </h3>

              <!-- Filters -->
              <div class="space-y-3 mb-4">
                <div>
                  <label class="block text-white text-sm font-semibold mb-2">กรองตามชั้นเรียน</label>
                  <select
                    v-model="studentFilters.gradeId"
                    @change="filterAvailableStudents"
                    class="w-full px-3 py-2 bg-white/10 border border-white/20 rounded-lg text-white focus:ring-2 focus:ring-purple-500 focus:border-transparent [&>option]:text-black"
                  >
                    <option value="">ทุกชั้นเรียน</option>
                    <option v-for="grade in grades" :key="grade.id" :value="grade.id">
                      {{ grade.gradeName }}
                    </option>
                  </select>
                </div>

                <div>
                  <label class="block text-white text-sm font-semibold mb-2">ค้นหานักเรียน</label>
                  <input
                    v-model="studentFilters.keyword"
                    @input="filterAvailableStudents"
                    type="text"
                    placeholder="พิมพ์ชื่อหรือชื่อเล่น..."
                    class="w-full px-3 py-2 bg-white/10 border border-white/20 rounded-lg text-white placeholder-white/50 focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  />
                </div>
              </div>

              <!-- Available Students List -->
              <div class="space-y-2 max-h-96 overflow-y-auto pr-2">
                <div v-if="isLoadingStudents" class="text-center py-8">
                  <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-white"></div>
                  <p class="mt-2 text-white/70">กำลังโหลด...</p>
                </div>

                <div v-else-if="filteredAvailableStudents.length === 0" class="text-center py-8">
                  <p class="text-white/70">ไม่พบนักเรียนที่สามารถเพิ่มได้</p>
                </div>

                <button
                  v-for="student in filteredAvailableStudents"
                  :key="student.id"
                  @click="handleAddStudent(student)"
                  :disabled="isAddingStudent"
                  class="w-full bg-white/5 hover:bg-white/10 rounded-lg p-3 transition-colors text-left flex items-center justify-between group disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <div class="flex items-center gap-3">
                    <div class="w-8 h-8 bg-gradient-to-br from-purple-500 to-pink-600 rounded-full flex items-center justify-center text-white text-xs font-bold">
                      {{ student.nickname?.charAt(0) || student.firstName?.charAt(0) }}
                    </div>
                    <div>
                      <p class="text-white font-semibold text-sm">{{ student.nickname }}</p>
                      <p class="text-white/70 text-xs">
                        {{ student.firstName }} {{ student.lastName }}
                        <span v-if="student.gradeName" class="ml-1 text-purple-300">({{ student.gradeName }})</span>
                      </p>
                    </div>
                  </div>
                  <Plus class="w-5 h-5 text-white/70 group-hover:text-white transition" />
                </button>
              </div>
            </div>

            <!-- Right: Current Students -->
            <div class="bg-white/10 rounded-2xl p-6 backdrop-blur-sm">
              <h3 class="text-xl font-bold text-white mb-4 flex items-center gap-2">
                <Users class="w-5 h-5" />
                นักเรียนในคลาส ({{ classStudents.length }})
              </h3>

              <div class="space-y-2 max-h-[500px] overflow-y-auto pr-2">
                <div v-if="classStudents.length === 0" class="text-center py-8">
                  <p class="text-white/70">ยังไม่มีนักเรียนในคลาส</p>
                </div>

                <div
                  v-for="(student, index) in classStudents"
                  :key="student.id"
                  class="bg-white/5 rounded-lg p-3 flex items-center justify-between group hover:bg-white/10 transition-colors"
                >
                  <div class="flex items-center gap-3">
                    <div class="w-8 h-8 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-full flex items-center justify-center text-white text-xs font-bold">
                      {{ index + 1 }}
                    </div>
                    <div>
                      <p class="text-white font-semibold text-sm">{{ student.nickname }}</p>
                      <p class="text-white/70 text-xs">
                        {{ student.firstName }} {{ student.lastName }}
                        <span v-if="student.gradeName" class="ml-1 text-blue-300">({{ student.gradeName }})</span>
                      </p>
                    </div>
                  </div>
                  <button
                    @click="handleDeactivateStudent(student)"
                    :disabled="isRemovingStudent"
                    class="p-2 text-red-400 hover:text-red-300 hover:bg-red-500/20 rounded-lg transition disabled:opacity-50 disabled:cursor-not-allowed"
                    title="ลบนักเรียน"
                  >
                    <X class="w-5 h-5" />
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div class="flex gap-4 justify-end pt-6 mt-6 border-t border-white/10">
            <button
              @click="closeManageStudentsModal"
              class="px-6 py-3 bg-slate-700 text-white font-semibold rounded-xl hover:bg-slate-600 transition-all shadow-lg"
            >
              ปิด
            </button>
          </div>
      </div>
      <template #footer>
        <!-- footer intentionally empty to preserve original actions -->
      </template>
    </ModalShell>

    <!-- Fixed corner notifications (bottom-right) -->
    <div :class="['fixed right-6 bottom-6 flex flex-col items-end gap-3', showCreateModal || showManageStudentsModal || showDetailModal ? 'z-[60]' : 'z-40']" aria-live="polite">
      <transition-group name="slide-fade" tag="div">
        <div v-if="successMessage" key="success" class="max-w-md w-full p-5 rounded-2xl bg-gradient-to-r from-green-500 to-emerald-600 text-white shadow-lg flex items-center gap-4">
          <CheckCircle class="w-7 h-7" />
          <div class="text-base font-semibold">{{ successMessage }}</div>
        </div>
        <div v-if="errorMessage" key="error" class="max-w-md w-full p-5 rounded-2xl bg-gradient-to-r from-red-500 to-pink-600 text-white shadow-lg flex items-center gap-4">
          <AlertCircle class="w-7 h-7" />
          <div class="flex-1 text-base font-semibold z-10">{{ errorMessage }}</div>
          <!-- เพิ่มปุ่มลูกศร -->
          <button 
            v-if="errorStudentId && !errorMessage.includes('อยู่ในคลาสอยู่แล้ว')"
            @click="redirectToStudentEdit"
            class="p-2 bg-white/20 hover:bg-white/30 rounded-lg transition-all group"
            title="ไปหน้าจัดการนักเรียน"
          >
            <svg class="w-5 h-5 group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6" />
            </svg>
          </button>
        </div>
      </transition-group>
    </div>
    <ConfirmDialog
      :show="showConfirmModal"
      :title="confirmModalConfig.title"
      :message="confirmModalConfig.message"
      :sub-message="confirmModalConfig.subMessage"
      :confirm-text="confirmModalConfig.confirmText"
      :cancel-text="confirmModalConfig.cancelText"
      :variant="confirmModalConfig.variant"
      :show-reason-input="confirmModalConfig.showReasonInput"
      :reason-options="confirmModalConfig.reasonOptions"
      @confirm="confirmModalConfig.onConfirm"
      @close="handleConfirmModalClose"
      ref="confirmDialogRef"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import {
  ChevronLeft,
  Calendar,
  Users,
  Clock,
  BookOpen,
  User,
  Plus,
  X,
  Eye,
  CheckCircle,
  XCircle,
  AlertCircle
} from 'lucide-vue-next';
import BackButton from '@/components/BackButton.vue';
import NavBar from '@/components/NavBar.vue';
import FilterAddClass from '@/components/FilterAddClass.vue';
import ModalShell from '@/components/ModalShell.vue';
import ConfirmModalInAddClass from '@/modals/ConfirmModalInAddClass.vue';
import ConfirmDialog from '@/components/ConfirmDialog.vue';
import api from '@/api.js';

// State
const router = useRouter();
const errorStudentId = ref(null);
const activeTab = ref('monthly');
const isLoading = ref(false);
const isSubmitting = ref(false);
const showCreateModal = ref(false);
const showDetailModal = ref(false);
const showManageStudentsModal = ref(false);
const selectedClass = ref(null);
const successMessage = ref('');
const errorMessage = ref('');
const isLoadingStudents = ref(false);
const isAddingStudent = ref(false);
const isRemovingStudent = ref(false);
const showConfirmModal = ref(false);
const confirmDialogRef = ref(null);
const confirmModalConfig = ref({
  title: '',
  message: '',
  subMessage: '',
  type: 'info',
  variant: 'warning',
  confirmText: 'ตกลง',
  cancelText: 'ยกเลิก',
  showReasonInput: false,
  reasonOptions: [],
  onConfirm: null
});

// Data
const monthlyClasses = ref([]);
const hourlyGroupClasses = ref([]);
const hourlyIndividualClasses = ref([]);
const subjects = ref([]);
const monthlySubtypes = ref([]);
const hourlyGroupSubtypes = ref([]);
const grades = ref([]);
const tutors = ref([]);
const students = ref([]);
const classStudents = ref([]);
const availableStudents = ref([]);
const filteredAvailableStudents = ref([]);

const studentFilters = ref({
  gradeId: '',
  keyword: ''
});

const showConfirmation = (config) => {
  confirmModalConfig.value = { 
    ...config, 
    showReasonInput: config.showReasonInput ?? false,
    reasonOptions: config.reasonOptions ?? [] 
  };
  showConfirmModal.value = true;
};

const handleConfirmModalClose = () => {
  showConfirmModal.value = false;
  confirmModalConfig.value.onConfirm = null;
};

const loadClassStudents = async (classId, classType) => {
  try {
    let endpoint = '';
    if (classType === 'monthly') {
      endpoint = `/monthly-classes/${classId}/students`;
    } else if (classType === 'hourly-group') {
      endpoint = `/hourly-group-classes/${classId}/students`;
    } else if (classType === 'hourly-individual') {
      endpoint = `/hourly-individual-classes/${classId}/students`;
    } else {
      return;
    }
    
    const res = await api.get(endpoint);
    classStudents.value = res.data.filter(student => student.isActive !== false);
  } catch (error) {
    console.error('Error loading class students:', error);
    classStudents.value = [];
  }
};

const loadAvailableStudents = async () => {
  isLoadingStudents.value = true;
  try {
    // ดึงนักเรียนทั้งหมด
    const allStudents = students.value;
    
    // กรองนักเรียนที่ไม่ได้อยู่ในคลาสแล้ว
    const studentIdsInClass = classStudents.value.map(s => s.id);
    availableStudents.value = allStudents
      .filter(s => !studentIdsInClass.includes(s.id) && s.isActive)
      .map(s => {
        // เพิ่ม gradeName เข้าไปด้วย
        const grade = grades.value.find(g => g.id === s.gradeId);
        return {
          ...s,
          gradeName: grade?.gradeName || ''
        };
      });
    
    // Set default filter ให้ตรงกับชั้นของคลาส (ถ้ามี)
    if (selectedClass.value?.gradeId && activeTab.value !== 'hourly-individual') {
      studentFilters.value.gradeId = selectedClass.value.gradeId;
    }
    
    filterAvailableStudents();
  } catch (error) {
    console.error('Error loading available students:', error);
  } finally {
    isLoadingStudents.value = false;
  }
};

const filterAvailableStudents = () => {
  let filtered = [...availableStudents.value];
  
  // กรองตามชั้นเรียน
  if (studentFilters.value.gradeId) {
    filtered = filtered.filter(s => s.gradeId === Number(studentFilters.value.gradeId));
  }
  
  // กรองตาม keyword
  if (studentFilters.value.keyword) {
    const keyword = studentFilters.value.keyword.toLowerCase();
    filtered = filtered.filter(s => {
      const fullName = `${s.firstName} ${s.lastName}`.toLowerCase();
      const nickname = (s.nickname || '').toLowerCase();
      const code = (s.studentCode || '').toLowerCase();
      return fullName.includes(keyword) || nickname.includes(keyword) || code.includes(keyword);
    });
  }
  
  filteredAvailableStudents.value = filtered;
};

const checkStudentCourseAccess = async (studentId, subjectId, classType) => {
  try {
    const res = await api.get(`/students/${studentId}/with-classes`);
    const coursePurchases = res.data.coursePurchases || [];
    
    const isPvSingleClass = selectedClass.value?.className?.startsWith('PV-เดี่ยว');
    
    let acceptedTypes;
    if (classType === 'hourly-group') {
      acceptedTypes = ['hourly_group', 'group'];
    } else if (classType === 'hourly-individual') {
      acceptedTypes = isPvSingleClass
        ? ['hourly_individual', 'individual'] // PV-เดี่ยว
        : ['hourly_individual_group', 'individual_group']; // PV-กลุ่ม
    }

    const matchingCourse = coursePurchases.find(c => 
      c.subjectId === subjectId && 
      acceptedTypes.includes(c.classType) &&
      c.isActive
    );
    
    if (!matchingCourse) {
      return { 
        hasAccess: false, 
        course: null, 
        reason: 'NO_PURCHASE',
        requiredType: acceptedTypes
      };
    }
    
    return { 
      hasAccess: true, 
      course: matchingCourse,
      hoursRemaining: matchingCourse.hoursPurchased - matchingCourse.hoursCompleted
    };
  } catch (error) {
    console.error('Error checking course access:', error);
    return { hasAccess: false, error: true };
  }
};

const checkAndPerformAddStudent = async (student) => {
  // เช็คว่านักเรียนซื้อคอร์สที่ตรงกับประเภทคลาสหรือยัง
  const courseCheck = await checkStudentCourseAccess(
    student.id, 
    selectedClass.value.subjectId,
    activeTab.value // 'monthly', 'hourly-group', หรือ 'hourly-individual'
  );

  if (!courseCheck.hasAccess) {
    // แสดง error message
    const subjectName = subjects.value.find(s => s.id === selectedClass.value.subjectId)?.subjectName || 'วิชานี้';
    
    // แยก error message ตามจำนวนนักเรียนในคลาส
    let classTypeText;
    if (activeTab.value === 'hourly-group') {
      classTypeText = 'กลุ่มรวม';
    } else if (activeTab.value === 'hourly-individual') {
      const isSingleStudent = selectedClass.value?.totalStudents === 1;
      classTypeText = isSingleStudent ? 'PV-เดี่ยว' : 'PV-กลุ่ม';
    }
    
    errorMessage.value = `นักเรียน ${student.nickname} ยังไม่เคยซื้อคอร์สนี้ กรุณาไปหน้าจัดการข้อมูลนักเรียนเพื่อเพิ่มคอร์ส`;
    errorStudentId.value = student.id;
    // errorMessage.value = `นักเรียน ${student.nickname} ยังไม่ซื้อคอร์สรายชั่วโมงวิชา${subjectName} ${classTypeText}\nกรุณาไปหน้าจัดการข้อมูลนักเรียนเพื่อเพิ่มคอร์ส`;
    setTimeout(() => (errorMessage.value = ''), 5000);
    showConfirmModal.value = false;
    confirmModalConfig.value.onConfirm = null;
    confirmDialogRef.value?.resetLoading();
    return;
  }

  // ถ้ามีคอร์สที่ถูกต้อง ให้เพิ่มนักเรียนได้
  await performAddStudent(student);
};

const handleAddStudent = async (student) => {
  // ตรวจสอบชั้นเรียนก่อน
  if (selectedClass.value.gradeId && student.gradeId !== selectedClass.value.gradeId) {
    const classGrade = grades.value.find(g => g.id === selectedClass.value.gradeId);
    const studentGrade = grades.value.find(g => g.id === student.gradeId);
    
  showConfirmation({
    title: 'คำเตือน',
    message: `นักเรียน ${student.nickname} อยู่ ${studentGrade?.gradeName || 'ชั้นไม่ทราบ'} แต่คลาสนี้เป็นคลาส ${classGrade?.gradeName || 'ชั้นไม่ทราบ'}\n\nต้องการเพิ่มนักเรียนเข้าคลาสนี้หรือไม่?`,
    type: 'warning',
    confirmText: 'ยืนยัน',
    cancelText: 'ยกเลิก',
    onConfirm: async () => {
      try {
        // เช็คคอร์สสำหรับคลาสรายชั่วโมงทั้งหมด (กลุ่มรวม + PV)
        if (activeTab.value === 'hourly-group' || activeTab.value === 'hourly-individual') {
          await checkAndPerformAddStudent(student);
        } else {
          // รายเดือนไม่ต้องเช็คคอร์ส
          await performAddStudent(student);
        }
        // ปิด modal หลังสำเร็จ
        showConfirmModal.value = false;
        confirmModalConfig.value.onConfirm = null;
      } finally {
        // รีเซ็ต loading state
        confirmDialogRef.value?.resetLoading();
      }
    }
  });
    return;
  }

  // ถ้าชั้นเรียนตรงกัน
  if (activeTab.value === 'monthly') {
    await performAddStudent(student);
    return;
  }

  // เช็คคอร์สสำหรับคลาสรายชั่วโมง (กลุ่มรวม + PV)
  await checkAndPerformAddStudent(student);
};

// สร้างฟังก์ชันแยกสำหรับการเพิ่มนักเรียนจริงๆ
const performAddStudent = async (student) => {
  isAddingStudent.value = true;
  try {
    let endpoint = '';
    if (activeTab.value === 'monthly') {
      endpoint = '/enrollments/monthly';
    } else if (activeTab.value === 'hourly-group') {
      endpoint = '/enrollments/hourly-group';
    } else if (activeTab.value === 'hourly-individual') {
      endpoint = '/enrollments/hourly-individual';
    }
    
    await api.post(endpoint, {
      classId: selectedClass.value.id,
      studentId: student.id
    });
    
    successMessage.value = `เพิ่มนักเรียน ${student.nickname} สำเร็จ!`;
    setTimeout(() => (successMessage.value = ''), 3000);
    
    // Reload data
    await loadClassStudents(selectedClass.value.id, activeTab.value);
    await loadAvailableStudents();
    await loadClasses(); // โหลดรายการคลาสใหม่เพื่ออัปเดต table

    // อัปเดต selectedClass ด้วยข้อมูลล่าสุด
    const updatedClass = getCurrentClasses().find(c => c.id === selectedClass.value.id);
    if (updatedClass) {
      selectedClass.value = { ...updatedClass };
    }
    
    // อัปเดต totalStudents ทันที
    selectedClass.value.totalStudents = classStudents.value.length;
    } catch (error) {
      console.error('Error adding student:', error);
      
      // เช็ค error message จาก backend
      const backendMessage = error.response?.data?.message || '';
      
      // ถ้า error เกี่ยวกับ Duplicate entry หรือ constraint violation
      if (backendMessage.includes('Duplicate entry') || 
          backendMessage.includes('uk_class_student') ||
          backendMessage.includes('constraint')) {
        errorMessage.value = 'นักเรียนที่คุณเลือกอยู่ในคลาสอยู่แล้ว';
      } else {
        errorMessage.value = backendMessage || 'เกิดข้อผิดพลาดในการเพิ่มนักเรียน';
      }
      
      setTimeout(() => (errorMessage.value = ''), 3000);
    } finally {
    isAddingStudent.value = false;
  }
};

// แทนที่ฟังก์ชัน handleRemoveStudent เดิมทั้งหมด
const handleDeactivateStudent = (student) => {
  showConfirmation({
    title: 'ปิดการใช้งานคลาส',
    message: `ต้องการปิดการใช้งานคลาสของนักเรียน ${student.nickname} หรือไม่?`,
    subMessage: 'กรุณาระบุหมายเหตุในการปิดการใช้งานคลาสนี้',
    variant: 'warning',
    confirmText: 'ปิดการใช้งาน',
    cancelText: 'ยกเลิก',
    showReasonInput: true,
    reasonOptions: [
      'น้องขอจบคอร์ส',
      'น้องขอหยุดเรียน',
      'แอดมินนำน้องเข้าผิดคลาส',
      'อื่น ๆ (แจ้งหัวหน้า)'
    ],
    onConfirm: async (reason) => {
      await performDeactivateStudent(student, reason);
    }
  });
};

const performDeactivateStudent = async (student, reason) => {
  isRemovingStudent.value = true;
  try {
    let endpoint = '';
    const enrollmentId = student.enrollmentId || student.id;
    
    if (activeTab.value === 'monthly') {
      endpoint = `/enrollments/monthly/${enrollmentId}/deactivate`;
    } else if (activeTab.value === 'hourly-group') {
      endpoint = `/enrollments/hourly-group/${enrollmentId}/deactivate`;
    } else if (activeTab.value === 'hourly-individual') {
      endpoint = `/enrollments/hourly-individual/${enrollmentId}/deactivate`;
    }
    
    await api.patch(endpoint, { reason: reason || 'น้องเลื่อนระดับชั้น' });
    
    successMessage.value = `ปิดการใช้งานคลาสของนักเรียน ${student.nickname} สำเร็จ!`;
    setTimeout(() => (successMessage.value = ''), 3000);
    
    await loadClassStudents(selectedClass.value.id, activeTab.value);
    await loadAvailableStudents();
    await loadClasses();

    const updatedClass = getCurrentClasses().find(c => c.id === selectedClass.value.id);
    if (updatedClass) {
      selectedClass.value = { ...updatedClass };
    }
    
    selectedClass.value.totalStudents = classStudents.value.length;

    showConfirmModal.value = false;
    confirmModalConfig.value.onConfirm = null;
    
    confirmDialogRef.value?.resetLoading();
  } catch (error) {
    console.error('Error deactivating student:', error);
    errorMessage.value = error.response?.data?.message || 'เกิดข้อผิดพลาดในการปิดการใช้งาน';
    setTimeout(() => (errorMessage.value = ''), 3000);
    confirmDialogRef.value?.resetLoading();
  } finally {
    isRemovingStudent.value = false;
  }
};

// แสดง confirmation สำหรับย้ายคลาส
const handlePromoteStudents = () => {
  if (!targetClass.value) {
    errorMessage.value = `ยังไม่มีคลาส ${selectedClass.value.subjectName} ${nextGrade.value.gradeName} ${selectedClass.value.subtypeName ? '- ' + selectedClass.value.subtypeName : ''} ในระบบ กรุณาสร้างคลาสก่อน`;
    setTimeout(() => (errorMessage.value = ''), 5000);
    return;
  }

  showConfirmation({
    title: 'ยืนยันการเลื่อนชั้น',
    message: `ต้องการเลื่อนนักเรียนทั้งหมด ${classStudents.value.length} คน จากคลาส "${selectedClass.value.className}" ไปยังคลาส "${targetClass.value.className}" หรือไม่?`,
    subMessage: 'ระบบจะ inactive คลาสเดิมให้นักเรียนที่ active ทั้งหมด และเพิ่มนักเรียนทุกคนเข้าคลาสใหม่',
    variant: 'info',
    confirmText: 'ยืนยัน',
    cancelText: 'ยกเลิก',
    showReasonInput: false,
    onConfirm: async () => {
      await performPromoteStudents();
    }
  });
};

// ทำการย้ายคลาสจริง
const performPromoteStudents = async () => {
  try {
    // 1. โหลดนักเรียนทั้งหมด (ทั้ง active และ inactive)
    const allStudentsEndpoint = activeTab.value === 'monthly' 
      ? `/monthly-classes/${selectedClass.value.id}/students`
      : `/hourly-group-classes/${selectedClass.value.id}/students`;
    
    const allStudentsRes = await api.get(allStudentsEndpoint);
    const allStudents = allStudentsRes.data; // รวมทั้ง active และ inactive
    
    const activeStudents = allStudents.filter(s => s.isActive);
    
    // 2. Deactivate คลาสเดิมให้นักเรียน active ทั้งหมด
    for (const student of activeStudents) {
      await performDeactivateStudent(student, 'น้องเลื่อนระดับชั้น');
    }
    
    // 3. เพิ่มนักเรียนทุกคนเข้าคลาสใหม่ พร้อมรักษาสถานะ isActive
    let addedCount = 0;
    let skippedCount = 0;
    
    const endpoint = activeTab.value === 'monthly' 
      ? '/enrollments/monthly/with-status' 
      : '/enrollments/hourly-group/with-status';
    
    for (const student of allStudents) {
      try {
        // เรียก API ใหม่ที่รองรับการส่ง isActive
        await api.post(endpoint, {
          classId: targetClass.value.id,
          studentId: student.id,
          isActive: student.isActive // รักษาสถานะเดิม
        });
        
        addedCount++;
      } catch (error) {
        // ถ้า error เป็นเพราะอยู่ในคลาสแล้ว ให้ข้าม
        if (error.response?.data?.message?.includes('อยู่ในคลาสอยู่แล้ว')) {
          skippedCount++;
        } else {
          console.error(`Error adding student ${student.id}:`, error);
        }
      }
    }
    
    successMessage.value = `เลื่อนชั้นสำเร็จ! เพิ่ม ${addedCount} คน${skippedCount > 0 ? `, ข้าม ${skippedCount} คน (อยู่ในคลาสแล้ว)` : ''}`;
    setTimeout(() => (successMessage.value = ''), 5000);
    
    await loadClassStudents(selectedClass.value.id, activeTab.value);
    await loadClasses();
    
    showConfirmModal.value = false;
    confirmModalConfig.value.onConfirm = null;
    confirmDialogRef.value?.resetLoading();
    
  } catch (error) {
    console.error('Error promoting students:', error);
    errorMessage.value = 'เกิดข้อผิดพลาดในการเลื่อนชั้น';
    setTimeout(() => (errorMessage.value = ''), 3000);
    confirmDialogRef.value?.resetLoading();
  }
};

// inactive คลาสให้นักเรียนทั้งหมด (สำหรับชั้นสูงสุด)
const handleDeactivateAllStudents = () => {
  showConfirmation({
    title: 'ยืนยันการปิดคลาส',
    message: `ต้องการปิดคลาส "${selectedClass.value.className}" ให้นักเรียนที่ active ทั้งหมด ${classStudents.value.filter(s => s.isActive).length} คน หรือไม่?`,
    subMessage: 'นักเรียนจะไม่สามารถเข้าเรียนคลาสนี้ได้อีก',
    variant: 'warning',
    confirmText: 'ยืนยัน',
    cancelText: 'ยกเลิก',
    showReasonInput: false,
    onConfirm: async () => {
      await performDeactivateAllStudents();
    }
  });
};

const performDeactivateAllStudents = async () => {
  try {
    const activeStudents = classStudents.value.filter(s => s.isActive);
    
    for (const student of activeStudents) {
      await performDeactivateStudent(student, 'น้องเลื่อนระดับชั้น');
    }
    
    successMessage.value = `ปิดคลาสสำเร็จ! ปิดให้ ${activeStudents.length} คน`;
    setTimeout(() => (successMessage.value = ''), 3000);
    
    await loadClassStudents(selectedClass.value.id, activeTab.value);
    await loadClasses();
    
    showConfirmModal.value = false;
    confirmModalConfig.value.onConfirm = null;
    confirmDialogRef.value?.resetLoading();
    
  } catch (error) {
    console.error('Error deactivating all students:', error);
    errorMessage.value = 'เกิดข้อผิดพลาดในการปิดคลาส';
    setTimeout(() => (errorMessage.value = ''), 3000);
    confirmDialogRef.value?.resetLoading();
  }
};

const performRemoveStudent = async (student) => {
  isRemovingStudent.value = true;
  try {
    let endpoint = '';
    if (activeTab.value === 'monthly') {
      endpoint = `/enrollments/monthly/${selectedClass.value.id}/students/${student.id}`;
    } else if (activeTab.value === 'hourly-group') {
      endpoint = `/enrollments/hourly-group/${selectedClass.value.id}/students/${student.id}`;
    } else if (activeTab.value === 'hourly-individual') {
      endpoint = `/enrollments/hourly-individual/${selectedClass.value.id}/students/${student.id}`;
    }
    
    await api.delete(endpoint);
    
    successMessage.value = `ลบนักเรียน ${student.nickname} สำเร็จ!`;
    setTimeout(() => (successMessage.value = ''), 3000);
    
    await loadClassStudents(selectedClass.value.id, activeTab.value);
    await loadAvailableStudents();
    await loadClasses(); // โหลดรายการคลาสใหม่เพื่ออัปเดต table

    // อัปเดต selectedClass ด้วยข้อมูลล่าสุด
    const updatedClass = getCurrentClasses().find(c => c.id === selectedClass.value.id);
    if (updatedClass) {
      selectedClass.value = { ...updatedClass };
    }
    
    // อัปเดต totalStudents ทันที
    selectedClass.value.totalStudents = classStudents.value.length;
  } catch (error) {
    console.error('Error removing student:', error);
    errorMessage.value = error.response?.data?.message || 'เกิดข้อผิดพลาดในการลบนักเรียน';
    setTimeout(() => (errorMessage.value = ''), 3000);
  } finally {
    isRemovingStudent.value = false;
  }
};

// Form
const newClass = ref({
  subjectId: '',
  subtypeId: '',
  gradeId: '',
  tutorId: '',
  studentIds: [],
  hoursTarget: ''
});

// Student autocomplete state
const studentSearchQuery = ref('');
const showStudentDropdown = ref(false);
const selectedStudents = ref([]);
const filteredStudents = ref([]);

const filters = ref({
  subjectId: null,
  gradeId: null,
  keyword: ''
});

const filteredClasses = computed(() => {
  let classes = getCurrentClasses();
  
  if (filters.value.subjectId) {
    classes = classes.filter(c => c.subjectId === filters.value.subjectId);
  }
  
  if (filters.value.gradeId) {
    classes = classes.filter(c => {
      // สำหรับคลาสที่มี gradeId (รายเดือน, กลุ่มรวม)
      if (c.gradeId) {
        return c.gradeId === filters.value.gradeId;
      }
      
      // สำหรับ PV-เดี่ยว/กลุ่ม ให้ดูจาก gradeId ของนักเรียนในคลาส
      if (activeTab.value === 'hourly-individual' && c.students) {
        return c.students.some(s => s.gradeId === filters.value.gradeId);
      }
      
      return false;
    });
  }
  
  if (filters.value.keyword) {
    const keyword = filters.value.keyword.toLowerCase();
    classes = classes.filter(c => 
      c.className?.toLowerCase().includes(keyword) ||
      c.subjectName?.toLowerCase().includes(keyword) ||
      c.gradeName?.toLowerCase().includes(keyword) ||
      c.tutorName?.toLowerCase().includes(keyword) ||
      (c.tutors && c.tutors.some(t => t.nickname?.toLowerCase().includes(keyword)))
    );
  }
  
  return classes;
});

// หา grade ถัดไป
const nextGrade = computed(() => {
  if (!selectedClass.value?.gradeId) return null;
  const currentGradeIndex = grades.value.findIndex(g => g.id === selectedClass.value.gradeId);
  if (currentGradeIndex === -1 || currentGradeIndex === grades.value.length - 1) return null;
  return grades.value[currentGradeIndex + 1];
});

// เช็คว่าเป็นชั้นสูงสุดหรือไม่
const isMaxGrade = computed(() => {
  if (!selectedClass.value?.gradeId) return false;
  
  // เช็คว่า gradeName เป็น "ม.6" หรือไม่
  const currentGrade = grades.value.find(g => g.id === selectedClass.value.gradeId);
  return currentGrade?.gradeName === 'ม.6';
});

// หาคลาสปลายทางที่มีอยู่แล้ว
const targetClass = computed(() => {
  if (!nextGrade.value || !selectedClass.value) return null;
  
  const classes = activeTab.value === 'monthly' ? monthlyClasses.value : hourlyGroupClasses.value;
  
  return classes.find(c => 
    c.subjectId === selectedClass.value.subjectId &&
    c.gradeId === nextGrade.value.id &&
    c.subtypeId === selectedClass.value.subtypeId
  );
});

// ชื่อปุ่มย้ายคลาส
const promoteButtonText = computed(() => {
  if (!selectedClass.value || !nextGrade.value) return '';
  return `เพิ่มน้องกลุ่มนี้ในคลาส ${selectedClass.value.subjectName} ${nextGrade.value.gradeName} ${selectedClass.value.subtypeName ? '- ' + selectedClass.value.subtypeName : ''}`;
});

const handleFilter = (newFilters) => {
  filters.value = { ...filters.value, ...newFilters };
};

// Methods
const loadData = async () => {
  isLoading.value = true;
  try {
    const [subjectsRes, gradesRes, studentsRes, monthlySubtypesRes, hourlyGroupSubtypesRes] = await Promise.all([
      api.get('/subjects'),
      api.get('/grades'),
      api.get('/students'),
      api.get('/monthly-subtypes'),
      api.get('/hourly-group-subtypes')
    ]);

    subjects.value = subjectsRes.data;
    grades.value = gradesRes.data;
    students.value = studentsRes.data;
    monthlySubtypes.value = monthlySubtypesRes.data;
    hourlyGroupSubtypes.value = hourlyGroupSubtypesRes.data;

    try {
      const tutorsRes = await api.get('/tutors');
      tutors.value = tutorsRes.data;
    } catch (error) {
      console.error('Error loading tutors:', error);
      tutors.value = [];
    }

    await loadClasses();
  } catch (error) {
    console.error('Error loading data:', error);
    errorMessage.value = 'ไม่สามารถโหลดข้อมูลได้';
  } finally {
    isLoading.value = false;
  }
};

const loadClasses = async () => {
  try {
    if (activeTab.value === 'monthly') {
      const res = await api.get('/monthly-classes');
      monthlyClasses.value = await Promise.all(
        res.data.map(async (cls) => {
          try {
            const studentsRes = await api.get(`/monthly-classes/${cls.id}/students`);
            const activeStudents = studentsRes.data.filter(s => s.isActive !== false);
            return {
              ...cls,
              totalStudents: activeStudents.length,
              isActive: activeStudents.length > 0
            };
          } catch (error) {
            console.error(`Error loading students for class ${cls.id}:`, error);
            return { ...cls, totalStudents: 0, isActive: false };
          }
        })
      );
    } else if (activeTab.value === 'hourly-group') {
      const res = await api.get('/hourly-group-classes');
      hourlyGroupClasses.value = await Promise.all(
        res.data.map(async (cls) => {
          try {
            const studentsRes = await api.get(`/hourly-group-classes/${cls.id}/students`);
            const activeStudents = studentsRes.data.filter(s => s.isActive !== false);
            return {
              ...cls,
              totalStudents: activeStudents.length,
              isActive: activeStudents.length > 0
            };
          } catch (error) {
            console.error(`Error loading students for class ${cls.id}:`, error);
            return { ...cls, totalStudents: 0, isActive: false };
          }
        })
      );
    } else {
      const res = await api.get('/hourly-individual-classes');
      hourlyIndividualClasses.value = await Promise.all(
        res.data.map(async (cls) => {
          const legacyTutor = cls.tutorId ? [{ id: cls.tutorId, nickname: cls.tutorName }] : [];

          try {
            const tutorsResponse = await api.get(`/hourly-individual-classes/${cls.id}/tutors`);
            if (tutorsResponse.data && tutorsResponse.data.length > 0) {
              cls.tutors = tutorsResponse.data;
              cls.totalTutors = tutorsResponse.data.length;
            } else if (legacyTutor.length > 0) {
              cls.tutors = legacyTutor;
              cls.totalTutors = legacyTutor.length;
            } else {
              cls.tutors = [];
              cls.totalTutors = 0;
            }

            const studentsRes = await api.get(`/hourly-individual-classes/${cls.id}/students`);
            const activeStudents = studentsRes.data.filter(s => s.isActive !== false);
            
            return {
              ...cls,
              students: activeStudents,
              totalStudents: activeStudents.length,
              isActive: activeStudents.length > 0
            };
          } catch (error) {
            if (legacyTutor.length > 0) {
              cls.tutors = legacyTutor;
              cls.totalTutors = legacyTutor.length;
            } else {
              console.warn(`Cannot fetch tutors for class ${cls.id}:`, error);
              cls.tutors = [];
              cls.totalTutors = 0;
            }
            return { ...cls, totalStudents: 0, isActive: false };
          }
        })
      );
    }

    await Promise.all(
      hourlyIndividualClasses.value.map(async (cls) => {
        // If the API already returned a single tutor via tutorId/tutorName, use it as a fallback.
        const legacyTutor = cls.tutorId ? [{ id: cls.tutorId, nickname: cls.tutorName }] : [];

        try {
          const tutorsResponse = await api.get(`/hourly-individual-classes/${cls.id}/tutors`);
          if (tutorsResponse.data && tutorsResponse.data.length > 0) {
            cls.tutors = tutorsResponse.data;
            cls.totalTutors = tutorsResponse.data.length;
          } else if (legacyTutor.length > 0) {
            cls.tutors = legacyTutor;
            cls.totalTutors = legacyTutor.length;
          } else {
            cls.tutors = [];
            cls.totalTutors = 0;
          }
        } catch (error) {
          // If the tutors endpoint fails, still show legacy tutor if present.
          if (legacyTutor.length > 0) {
            cls.tutors = legacyTutor;
            cls.totalTutors = legacyTutor.length;
          } else {
            console.warn(`Cannot fetch tutors for class ${cls.id}:`, error);
            cls.tutors = [];
            cls.totalTutors = 0;
          }
        }
      })
    );

  } catch (error) {
    console.error('Error loading classes:', error);
  }
};

const getCurrentClasses = () => {
  if (activeTab.value === 'monthly') return monthlyClasses.value;
  if (activeTab.value === 'hourly-group') return hourlyGroupClasses.value;
  return hourlyIndividualClasses.value;
};

const getCurrentSubtypes = () => {
  if (activeTab.value === 'monthly') return monthlySubtypes.value;
  return hourlyGroupSubtypes.value;
};

const getTabLabel = () => {
  if (activeTab.value === 'monthly') return 'รายเดือน';
  if (activeTab.value === 'hourly-group') return 'กลุ่มรวม';
  return 'PV-เดี่ยว / PV-กลุ่ม';
};

const openCreateModal = () => {
  resetForm();
  showCreateModal.value = true;
};

const closeCreateModal = () => {
  showCreateModal.value = false;
  resetForm();
};

const resetForm = () => {
  newClass.value = {
    subjectId: '',
    subtypeId: '',
    gradeId: '',
    tutorId: '',
    studentIds: [],
    hoursTarget: ''
  };
  studentSearchQuery.value = '';
  selectedStudents.value = [];
  filteredStudents.value = [];
  showStudentDropdown.value = false;
};

// Student autocomplete methods
const handleStudentSearch = () => {
  const query = studentSearchQuery.value.trim().toLowerCase();
  
  if (query.length === 0) {
    filteredStudents.value = [];
    showStudentDropdown.value = false;
    return;
  }
  
  filteredStudents.value = students.value.filter(student => {
    const fullName = `${student.firstName} ${student.lastName}`.toLowerCase();
    const nickname = (student.nickname || '').toLowerCase();
    const code = (student.studentCode || '').toLowerCase();
    
    return fullName.includes(query) || 
           nickname.includes(query) || 
           code.includes(query);
  }).slice(0, 10);
  
  showStudentDropdown.value = true;
};

const handleStudentFocus = () => {
  if (studentSearchQuery.value.trim().length > 0) {
    handleStudentSearch();
  }
};

const selectStudent = (student) => {
  // ตรวจสอบว่าเลือกแล้วหรือยัง
  if (isStudentAlreadySelected(student.id)) {
    return;
  }
  
  // เพิ่มนักเรียนเข้า array
  selectedStudents.value.push(student);
  newClass.value.studentIds.push(student.id);
  
  // Clear search
  studentSearchQuery.value = '';
  showStudentDropdown.value = false;
  filteredStudents.value = [];
};

const isStudentAlreadySelected = (studentId) => {
  return selectedStudents.value.some(s => s.id === studentId);
};

const removeSelectedStudent = (studentId) => {
  selectedStudents.value = selectedStudents.value.filter(s => s.id !== studentId);
  newClass.value.studentIds = newClass.value.studentIds.filter(id => id !== studentId);
};

const clearSelectedStudent = () => {
  selectedStudents.value = [];
  newClass.value.studentIds = [];
  studentSearchQuery.value = '';
  filteredStudents.value = [];
  showStudentDropdown.value = false;
};

const handleCreateClass = async () => {
  if (!newClass.value.subjectId) {
    errorMessage.value = 'กรุณาเลือกวิชา';
    setTimeout(() => (errorMessage.value = ''), 3000);
    return;
  }

  if (activeTab.value !== 'hourly-individual' && !newClass.value.subtypeId) {
    errorMessage.value = 'กรุณาเลือกประเภทคลาส';
    setTimeout(() => (errorMessage.value = ''), 3000);
    return;
  }

  if (activeTab.value !== 'hourly-individual' && !newClass.value.gradeId) {
    errorMessage.value = 'กรุณาเลือกชั้นเรียน';
    setTimeout(() => (errorMessage.value = ''), 3000);
    return;
  }

  if (activeTab.value === 'hourly-individual' && selectedStudents.value.length === 0) {
    errorMessage.value = 'กรุณาเลือกนักเรียนอย่างน้อย 1 คน';
    setTimeout(() => (errorMessage.value = ''), 3000);
    return;
  }

  if (activeTab.value === 'hourly-individual') {
    const subjectId = parseInt(newClass.value.subjectId);
    const subjectName = subjects.value.find(s => s.id === subjectId)?.subjectName || 'วิชานี้';
    
    // กำหนดว่าเป็นคลาสเดี่ยวหรือกลุ่ม
    const isSingleStudent = selectedStudents.value.length === 1;
    const requiredClassType = isSingleStudent ? 'hourly_individual' : 'hourly_individual_group';
    const classTypeLabel = isSingleStudent ? 'PV-เดี่ยว' : 'PV-กลุ่ม';
    
    // ตรวจสอบนักเรียนทุกคน
    for (const student of selectedStudents.value) {
      // ดึงข้อมูลคอร์สที่นักเรียนซื้อ
      const res = await api.get(`/students/${student.id}/with-classes`);
      const coursePurchases = res.data.coursePurchases || [];
      
      // หาคอร์สที่ตรงกับวิชาและประเภทที่ต้องการ
      const acceptedClassTypes = isSingleStudent 
        ? ['hourly_individual', 'INDIVIDUAL', 'individual'] 
        : ['hourly_individual_group', 'INDIVIDUAL_GROUP', 'individual_group', 'hourly_group', 'GROUP', 'group'];

      const matchingCourse = coursePurchases.find(c => 
        c.subjectId === subjectId && 
        acceptedClassTypes.includes(c.classType) &&
        c.isActive
      );
      
      if (!matchingCourse) {
        errorMessage.value = `นักเรียน ${student.nickname} ยังไม่เคยซื้อคอร์สนี้ กรุณาไปหน้าจัดการข้อมูลนักเรียนเพื่อเพิ่มคอร์ส`;
        errorStudentId.value = student.id;
        // errorMessage.value = `นักเรียน ${student.nickname} ยังไม่ซื้อคอร์สรายชั่วโมงวิชา${subjectName} ${classTypeLabel}\nกรุณาไปหน้าจัดการข้อมูลนักเรียนเพื่อเพิ่มคอร์ส`;
        setTimeout(() => (errorMessage.value = ''), 5000);
        return;
      }
    }
  }

  isSubmitting.value = true;
  
  try {
    // Duplicate check: prevent creating a class that already exists
    const getId = (obj, idName, relName) => {
      if (!obj) return null;
      if (obj[idName] !== undefined && obj[idName] !== null) return Number(obj[idName]);
      if (relName && obj[relName] && obj[relName].id !== undefined) return Number(obj[relName].id);
      return null;
    };

    const isDuplicate = () => {
      const subjectId = Number(newClass.value.subjectId || 0);
      const gradeId = Number(newClass.value.gradeId || 0);
      const subtypeId = Number(newClass.value.subtypeId || 0);

      if (activeTab.value === 'monthly') {
        return monthlyClasses.value.some(c => {
          const cSubject = getId(c, 'subjectId', 'subject');
          const cGrade = getId(c, 'gradeId', 'grade');
          const cSubtype = getId(c, 'subtypeId', 'subtype');
          const sameSubject = cSubject === subjectId;
          const sameGrade = cGrade === gradeId;
          const sameSubtype = cSubtype === subtypeId;
          return sameSubject && sameGrade && sameSubtype;
        });
      }

      if (activeTab.value === 'hourly-group') {
        return hourlyGroupClasses.value.some(c => {
          const cSubject = getId(c, 'subjectId', 'subject');
          const cGrade = getId(c, 'gradeId', 'grade');
          const cSubtype = getId(c, 'subtypeId', 'subtype');
          const sameSubject = cSubject === subjectId;
          const sameGrade = cGrade === gradeId;
          const sameSubtype = cSubtype === subtypeId;
          return sameSubject && sameGrade && sameSubtype;
        });
      }

      if (activeTab.value === 'hourly-individual') {
        // For individual classes, check subject + students (exact match) + status
        const studentIds = (newClass.value.studentIds || []).sort(); // เรียงลำดับเพื่อเปรียบเทียบ
        const isNewSingleStudent = studentIds.length === 1;
        
        return hourlyIndividualClasses.value.some(c => {
          const cSubject = getId(c, 'subjectId', 'subject');
          const sameSubject = cSubject === subjectId;
          const sameStatus = (c.isActive === undefined ? true : Boolean(c.isActive)) === true;
          
          if (!sameSubject || !sameStatus) return false;
          
          // ดึงรายชื่อนักเรียนทั้งหมดในคลาสนี้
          const classStudentIds = (c.students || [])
            .filter(s => s.isActive !== false)
            .map(s => s.id)
            .sort();
          
          const isExistingSingleStudent = classStudentIds.length === 1;
          
          // กรณี PV-เดี่ยว: ต้องมีนักเรียนคนเดียวกันพอดี
          if (isNewSingleStudent && isExistingSingleStudent) {
            return classStudentIds[0] === studentIds[0];
          }
          
          // กรณี PV-กลุ่ม: ต้องมีนักเรียนเหมือนกันทุกคน (exact match)
          if (!isNewSingleStudent && !isExistingSingleStudent) {
            if (classStudentIds.length !== studentIds.length) return false;
            return classStudentIds.every((id, idx) => id === studentIds[idx]);
          }
          
          // กรณีจำนวนนักเรียนไม่เท่ากัน = ไม่ซ้ำ
          return false;
        });
      }

      return false;
    };

    if (isDuplicate()) {
      errorMessage.value = 'ไม่สามารถสร้างคลาสได้ มีคลาสดังกล่าวในระบบอยู่แล้ว';
      setTimeout(() => (errorMessage.value = ''), 4000);
      isSubmitting.value = false;
      return;
    }
    const payload = {
      subjectId: parseInt(newClass.value.subjectId),
      tutorId: newClass.value.tutorId ? parseInt(newClass.value.tutorId) : null
    };

    // เพิ่ม gradeId เฉพาะคลาสที่ไม่ใช่เดี่ยว
    if (activeTab.value !== 'hourly-individual') {
      payload.gradeId = parseInt(newClass.value.gradeId);
    }

    if (activeTab.value === 'monthly') {
      const subject = subjects.value.find(s => s.id === parseInt(newClass.value.subjectId));
      const grade = grades.value.find(g => g.id === parseInt(newClass.value.gradeId));
      const subtype = monthlySubtypes.value.find(st => st.id === parseInt(newClass.value.subtypeId));
        
      const subjectName = subject?.subjectName || 'วิชา';
      const gradeName = grade?.gradeName || 'ชั้นเรียน';
      const subtypeName = subtype?.subtypeName || 'ประเภท';
        
      const className = `${subjectName} ${gradeName} - ${subtypeName}`;
      payload.className = className;
      payload.subtypeId = parseInt(newClass.value.subtypeId);
      await api.post('/monthly-classes', payload);
    } else if (activeTab.value === 'hourly-group') {
      const subject = subjects.value.find(s => s.id === parseInt(newClass.value.subjectId));
      const grade = grades.value.find(g => g.id === parseInt(newClass.value.gradeId));
      const subtype = hourlyGroupSubtypes.value.find(st => st.id === parseInt(newClass.value.subtypeId));
      
      const subjectName = subject?.subjectName || 'วิชา';
      const gradeName = grade?.gradeName || 'ชั้นเรียน';
      const subtypeName = subtype?.subtypeName || 'ประเภท';
      
      const className = `${subjectName} ${gradeName} - ${subtypeName}`;
      payload.className = className;
      payload.subtypeId = parseInt(newClass.value.subtypeId);
      await api.post('/hourly-group-classes', payload);
    } else {
      const subject = subjects.value.find(s => s.id === parseInt(newClass.value.subjectId));
      const subjectName = subject?.subjectName || 'วิชา';
      
      // ถ้ามีนักเรียนหลายคน ให้รวมชื่อ
      const studentNames = selectedStudents.value.map(s => s.nickname).join(', ');
      const className = selectedStudents.value.length === 1
        ? `PV-เดี่ยว - ${studentNames} (${subjectName})`
        : `PV-กลุ่ม - ${studentNames} (${subjectName})`;
      
      payload.className = className;
      payload.studentIds = selectedStudents.value.map(s => s.id);
      await api.post('/hourly-individual-classes', payload);
    }

    successMessage.value = 'สร้างคลาสสำเร็จ!';
    closeCreateModal();
    await loadClasses();

    setTimeout(() => (successMessage.value = ''), 3000);
  } catch (error) {
    console.error('Error creating class:', error);
    errorMessage.value = error.response?.data?.message || 'เกิดข้อผิดพลาดในการสร้างคลาส';
    setTimeout(() => (errorMessage.value = ''), 3000);
  } finally {
    isSubmitting.value = false;
  }
};

const redirectToStudentEdit = () => {
  if (!errorStudentId.value) return;
  
  router.push({
    name: 'StudentManagementLikeTutor',
    query: { studentId: errorStudentId.value }
  });
};

const viewClassDetail = async (cls) => {
  selectedClass.value = cls;
  
  // โหลดข้อมูลนักเรียนก่อน
  if (activeTab.value === 'monthly' || activeTab.value === 'hourly-group' || activeTab.value === 'hourly-individual') {
    await loadClassStudents(cls.id, activeTab.value);
  }
  
  // แล้วค่อยเปิด modal
  showDetailModal.value = true;
};

const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedClass.value = null;
  classStudents.value = [];
};

const openManageStudentsModal = () => {
  showManageStudentsModal.value = true;
  loadAvailableStudents();
};

const closeManageStudentsModal = () => {
  showManageStudentsModal.value = false;
  studentFilters.value = { gradeId: '', keyword: '' };
  filteredAvailableStudents.value = [];
  availableStudents.value = [];
};

const handleClickOutside = (event) => {
  const autocomplete = event.target.closest('.student-autocomplete');
  if (!autocomplete) {
    showStudentDropdown.value = false;
  }
};

// Lifecycle
onMounted(() => {
  loadData();
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});

// Watchers
watch(activeTab, () => {
  if (activeTab.value) {
    loadClasses();
  }
});
</script>

<style scoped>
</style>