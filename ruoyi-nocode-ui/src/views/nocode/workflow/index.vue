<template>
  <div class="workflow-designer-container">
    <!-- Header -->
    <div class="workflow-designer-header">
      <div class="header-left">
        <h2>Workflow Designer</h2>
        <el-tag v-if="workflowConfig.status" :type="getStatusType(workflowConfig.status)" size="small">
          {{ workflowConfig.status }}
        </el-tag>
        <el-tag v-if="workflowConfig.version" type="info" size="small">v{{ workflowConfig.version }}</el-tag>
      </div>
      <div class="header-actions">
        <el-button type="text" @click="handleList">Workflow List</el-button>
        <el-button type="primary" @click="handleNewWorkflow">New Workflow</el-button>
        <el-button @click="handlePreview">Preview XML</el-button>
        <el-button type="success" @click="handleSave" :loading="saving">Save</el-button>
        <el-button type="warning" @click="handleDeploy" :disabled="!currentWorkflow.id">Deploy</el-button>
      </div>
    </div>

    <!-- Main Content -->
    <div class="workflow-designer-main">
      <!-- Left Panel: Node Library -->
      <div class="node-library">
        <div class="library-section">
          <h3>Events</h3>
          <div class="node-list">
            <div
              v-for="node in eventNodes"
              :key="node.type"
              class="node-item"
              draggable="true"
              @dragstart="onDragStart($event, node)"
              @click="addNode(node)"
            >
              <i :class="node.icon"></i>
              <span>{{ node.label }}</span>
            </div>
          </div>
        </div>
        <div class="library-section">
          <h3>Tasks</h3>
          <div class="node-list">
            <div
              v-for="node in taskNodes"
              :key="node.type"
              class="node-item"
              draggable="true"
              @dragstart="onDragStart($event, node)"
              @click="addNode(node)"
            >
              <i :class="node.icon"></i>
              <span>{{ node.label }}</span>
            </div>
          </div>
        </div>
        <div class="library-section">
          <h3>Gateways</h3>
          <div class="node-list">
            <div
              v-for="node in gatewayNodes"
              :key="node.type"
              class="node-item"
              draggable="true"
              @dragstart="onDragStart($event, node)"
              @click="addNode(node)"
            >
              <i :class="node.icon"></i>
              <span>{{ node.label }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Center Panel: Workflow Canvas -->
      <div class="workflow-canvas" ref="canvasContainer">
        <div class="canvas-toolbar">
          <el-button-group size="small">
            <el-button @click="zoomIn" title="Zoom In"><i class="el-icon-zoom-in"></i></el-button>
            <el-button @click="zoomOut" title="Zoom Out"><i class="el-icon-zoom-out"></i></el-button>
            <el-button @click="zoomReset" title="Reset Zoom">{{ zoom }}%</el-button>
            <el-button @click="autoLayout" title="Auto Layout"><i class="el-icon-s-grid"></i></el-button>
          </el-button-group>
          <el-button-group size="small" style="margin-left: 12px;">
            <el-button @click="deleteSelectedNode" :disabled="!selectedNode" type="danger" title="Delete Selected">
              <i class="el-icon-delete"></i>
            </el-button>
            <el-button @click="clearAll" type="danger" title="Clear All">
              <i class="el-icon-delete-solid"></i>
            </el-button>
          </el-button-group>
        </div>
        <div
          class="canvas-content"
          ref="canvas"
          :style="{ transform: `scale(${zoom / 100})` }"
          @drop="onDrop"
          @dragover="onDragOver"
          @click="selectNode(null)"
          @mousemove="onCanvasMouseMove"
          @mouseup="onCanvasMouseUp"
        >
          <svg class="edge-svg" ref="edgeSvg">
            <defs>
              <marker id="arrowhead" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
                <polygon points="0 0, 10 3.5, 0 7" fill="#409eff"/>
              </marker>
              <marker id="arrowhead-selected" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
                <polygon points="0 0, 10 3.5, 0 7" fill="#f56c6c"/>
              </marker>
            </defs>
            <g v-for="(edge, index) in edges" :key="'edge-' + index">
              <path
                :d="getEdgePath(edge)"
                :stroke="selectedEdge && selectedEdge.index === index ? '#f56c6c' : '#409eff'"
                :stroke-width="selectedEdge && selectedEdge.index === index ? 3 : 2"
                fill="none"
                :marker-end="selectedEdge && selectedEdge.index === index ? 'url(#arrowhead-selected)' : 'url(#arrowhead)'"
                @click.stop="selectEdge(edge, index)"
                class="edge-path"
              />
              <text
                :x="getEdgeMidPoint(edge).x"
                :y="getEdgeMidPoint(edge).y - 10"
                class="edge-label"
                @click.stop="selectEdge(edge, index)"
              >
                {{ edge.condition || '' }}
              </text>
            </g>
            <path
              v-if="connecting && tempEdge"
              :d="tempEdge"
              stroke="#667eea"
              stroke-width="2"
              stroke-dasharray="5,5"
              fill="none"
            />
          </svg>

          <!-- Nodes -->
          <div
            v-for="node in nodes"
            :key="node.id"
            class="workflow-node"
            :class="[
              node.type,
              {
                selected: selectedNode && selectedNode.id === node.id,
                'has-error': node.error
              }
            ]"
            :style="{ left: node.x + 'px', top: node.y + 'px' }"
            @click.stop="selectNode(node)"
            @mousedown="onNodeMouseDown($event, node)"
            @dblclick="onNodeDoubleClick(node)"
          >
            <!-- Connection Points -->
            <div
              class="connection-point source"
              @mouseup.stop="onConnectionEnd(node, 'source')"
            ></div>
            <div
              class="connection-point target"
              @mousedown.stop="onConnectionStart(node, 'target')"
            ></div>

            <div class="node-icon">
              <i :class="getNodeIcon(node.type)"></i>
            </div>
            <div class="node-label">{{ node.name }}</div>
            <div class="node-type-badge">{{ getNodeTypeLabel(node.type) }}</div>

            <!-- Error indicator -->
            <div v-if="node.error" class="node-error">
              <i class="el-icon-warning"></i>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Panel: Properties -->
      <div class="properties-panel">
        <el-tabs v-model="propertyTab" type="border-card">
          <el-tab-pane label="Node Properties" name="node">
            <div v-if="selectedNode" class="property-form">
              <el-form label-width="100px" size="small">
                <el-form-item label="Node ID">
                  <el-input v-model="selectedNode.id" disabled></el-input>
                </el-form-item>
                <el-form-item label="Node Name">
                  <el-input v-model="selectedNode.name" placeholder="Node name"></el-input>
                </el-form-item>
                <el-form-item label="Node Type">
                  <el-input :value="getNodeTypeLabel(selectedNode.type)" disabled></el-input>
                </el-form-item>
                <el-divider>Position</el-divider>
                <el-form-item label="X">
                  <el-input-number v-model="selectedNode.x" :min="0" size="small"></el-input-number>
                </el-form-item>
                <el-form-item label="Y">
                  <el-input-number v-model="selectedNode.y" :min="0" size="small"></el-input-number>
                </el-form-item>

                <!-- Start Event Properties -->
                <template v-if="selectedNode.type === 'startEvent'">
                  <el-divider>Event Definition</el-divider>
                  <el-form-item label="Event Type">
                    <el-select v-model="selectedNode.eventType">
                      <el-option label="None" value=""></el-option>
                      <el-option label="Timer" value="timer"></el-option>
                      <el-option label="Signal" value="signal"></el-option>
                      <el-option label="Message" value="message"></el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="Timer Value" v-if="selectedNode.eventType === 'timer'">
                    <el-input v-model="selectedNode.timerValue" placeholder="如: R3/PT10H"></el-input>
                  </el-form-item>
                </template>

                <!-- End Event Properties -->
                <template v-if="selectedNode.type === 'endEvent'">
                  <el-divider>Event Definition</el-divider>
                  <el-form-item label="End Type">
                    <el-select v-model="selectedNode.endType">
                      <el-option label="None" value=""></el-option>
                      <el-option label="Cancel" value="cancel"></el-option>
                      <el-option label="Error" value="error"></el-option>
                      <el-option label="Terminate" value="terminate"></el-option>
                    </el-select>
                  </el-form-item>
                </template>

                <!-- User Task Properties -->
                <template v-if="selectedNode.type === 'userTask'">
                  <el-divider>Task Settings</el-divider>
                  <el-form-item label="Assignee">
                    <el-input v-model="selectedNode.assignee" placeholder="user_id">
                      <el-button slot="append" icon="el-icon-user"></el-button>
                    </el-input>
                  </el-form-item>
                  <el-form-item label="Candidate Users">
                    <el-input v-model="selectedNode.candidateUsers" placeholder="user1,user2"></el-input>
                  </el-form-item>
                  <el-form-item label="Candidate Groups">
                    <el-input v-model="selectedNode.candidateGroups" placeholder="group1,group2"></el-input>
                  </el-form-item>
                  <el-form-item label="Form Key">
                    <el-input v-model="selectedNode.formKey" placeholder="form_key">
                      <el-button slot="append" icon="el-icon-document"></el-button>
                    </el-input>
                  </el-form-item>
                  <el-form-item label="Due Date">
                    <el-input v-model="selectedNode.dueDate" placeholder="P10D"></el-input>
                  </el-form-item>
                  <el-form-item label="Priority">
                    <el-input-number v-model="selectedNode.priority" :min="0" :max="100" size="small"></el-input-number>
                  </el-form-item>
                </template>

                <!-- Service Task Properties -->
                <template v-if="selectedNode.type === 'serviceTask'">
                  <el-divider>Service Task</el-divider>
                  <el-form-item label="Operation Type">
                    <el-select v-model="selectedNode.operationType">
                      <el-option label="Expression" value="expression"></el-option>
                      <el-option label="Delegate Expression" value="delegateExpression"></el-option>
                      <el-option label="Class" value="class"></el-option>
                      <el-option label="Connector" value="connector"></el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="Expression" v-if="selectedNode.operationType === 'expression'">
                    <el-input v-model="selectedNode.expression" placeholder="${myBean.doSomething()}"></el-input>
                  </el-form-item>
                  <el-form-item label="Delegate" v-if="selectedNode.operationType === 'delegateExpression'">
                    <el-input v-model="selectedNode.delegateExpression" placeholder="${myDelegate}"></el-input>
                  </el-form-item>
                  <el-form-item label="Class" v-if="selectedNode.operationType === 'class'">
                    <el-input v-model="selectedNode.className" placeholder="com.example.MyDelegate"></el-input>
                  </el-form-item>
                  <el-form-item label="Result Variable">
                    <el-input v-model="selectedNode.resultVariable" placeholder="variableName"></el-input>
                  </el-form-item>
                </template>

                <!-- Script Task Properties -->
                <template v-if="selectedNode.type === 'scriptTask'">
                  <el-divider>Script Task</el-divider>
                  <el-form-item label="Script Format">
                    <el-select v-model="selectedNode.scriptFormat">
                      <el-option label="JavaScript" value="javascript"></el-option>
                      <el-option label="Groovy" value="groovy"></el-option>
                      <el-option label="Python" value="python"></el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="Script">
                    <el-input
                      type="textarea"
                      v-model="selectedNode.script"
                      placeholder="// Your script code"
                      :rows="4"
                    ></el-input>
                  </el-form-item>
                  <el-form-item label="Result Variable">
                    <el-input v-model="selectedNode.resultVariable" placeholder="variableName"></el-input>
                  </el-form-item>
                </template>

                <!-- Gateway Properties -->
                <template v-if="['exclusiveGateway', 'parallelGateway', 'inclusiveGateway'].includes(selectedNode.type)">
                  <el-divider>Gateway Settings</el-divider>
                  <el-form-item label="Gateway Direction">
                    <el-select v-model="selectedNode.gatewayDirection">
                      <el-option label="Diverging (OUT)" value="diverging"></el-option>
                      <el-option label="Converging (IN)" value="converging"></el-option>
                    </el-select>
                  </el-form-item>
                </template>

                <!-- Exclusive Gateway Specific -->
                <template v-if="selectedNode.type === 'exclusiveGateway'">
                  <el-form-item label="Default Flow">
                    <el-select v-model="selectedNode.defaultFlow" placeholder="Select default flow" clearable>
                      <el-option
                        v-for="edge in getOutgoingEdges(selectedNode.id)"
                        :key="edge.id"
                        :label="edge.label || edge.target"
                        :value="edge.id"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </template>

                <el-divider>Documentation</el-divider>
                <el-form-item label="Description">
                  <el-input
                    type="textarea"
                    v-model="selectedNode.description"
                    placeholder="Node description"
                    :rows="2"
                  ></el-input>
                </el-form-item>
              </el-form>
            </div>
            <div v-else class="no-selection">
              <i class="el-icon-cursor"></i>
              <p>Select a node to edit its properties</p>
            </div>
          </el-tab-pane>

          <el-tab-pane label="Sequence Flow" name="flow">
            <div v-if="selectedEdge" class="property-form">
              <el-form label-width="100px" size="small">
                <el-form-item label="Flow ID">
                  <el-input v-model="selectedEdge.id" disabled></el-input>
                </el-form-item>
                <el-form-item label="Source">
                  <el-input :value="selectedEdge.source" disabled></el-input>
                </el-form-item>
                <el-form-item label="Target">
                  <el-input :value="selectedEdge.target" disabled></el-input>
                </el-form-item>
                <el-divider>Flow Condition</el-divider>
                <el-form-item label="Condition Type">
                  <el-select v-model="selectedEdge.conditionType">
                    <el-option label="None" value=""></el-option>
                    <el-option label="Expression" value="expression"></el-option>
                    <el-option label="Script" value="script"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="Expression" v-if="selectedEdge.conditionType === 'expression'">
                  <el-input
                    v-model="selectedEdge.condition"
                    placeholder="${gender == 'male'}"
                  ></el-input>
                </el-form-item>
                <el-form-item label="Script" v-if="selectedEdge.conditionType === 'script'">
                  <el-input
                    type="textarea"
                    v-model="selectedEdge.script"
                    placeholder="// return condition;"
                    :rows="3"
                  ></el-input>
                </el-form-item>
                <el-form-item label="Skip Expression">
                  <el-input
                    v-model="selectedEdge.skipExpression"
                    placeholder="${ 'true' == 'true' }"
                  ></el-input>
                </el-form-item>
                <el-divider>Appearance</el-divider>
                <el-form-item label="Flow Label">
                  <el-input v-model="selectedEdge.label" placeholder="Flow label"></el-input>
                </el-form-item>
              </el-form>
            </div>
            <div v-else class="no-selection">
              <i class="el-icon-sort"></i>
              <p>Select a flow to edit its properties</p>
              <p class="hint">Click on a connection line to select it</p>
            </div>
          </el-tab-pane>

          <el-tab-pane label="Process Settings" name="process">
            <el-form label-width="100px" size="small">
              <el-form-item label="Process ID">
                <el-input v-model="workflowConfig.processKey" placeholder="process_key"></el-input>
              </el-form-item>
              <el-form-item label="Process Name">
                <el-input v-model="workflowConfig.name" placeholder="Process name"></el-input>
              </el-form-item>
              <el-form-item label="Description">
                <el-input
                  type="textarea"
                  v-model="workflowConfig.description"
                  placeholder="Process description"
                  :rows="3"
                ></el-input>
              </el-form-item>
              <el-divider>Execution</el-divider>
              <el-form-item label="Start Form Key">
                <el-input v-model="workflowConfig.startFormKey" placeholder="form_key"></el-input>
              </el-form-item>
              <el-form-item label="Version Tag">
                <el-input v-model="workflowConfig.versionTag" placeholder="如: 1.0"></el-input>
              </el-form-item>
              <el-form-item label="History Level">
                <el-select v-model="workflowConfig.historyLevel">
                  <el-option label="None" value="none"></el-option>
                  <el-option label="Activity" value="activity"></el-option>
                  <el-option label="Audit" value="audit"></el-option>
                  <el-option label="Full" value="full"></el-option>
                </el-select>
              </el-form-item>
              <el-divider>Category</el-divider>
              <el-form-item label="Process Category">
                <el-input v-model="workflowConfig.category" placeholder="Category"></el-input>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- Workflow List Dialog -->
    <el-dialog title="Workflow List" :visible.sync="workflowListVisible" width="90%">
      <el-table :data="workflowList" border>
        <el-table-column prop="name" label="Name" width="150"></el-table-column>
        <el-table-column prop="processKey" label="Process Key" width="150"></el-table-column>
        <el-table-column prop="description" label="Description"></el-table-column>
        <el-table-column prop="status" label="Status" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="version" label="Version" width="80"></el-table-column>
        <el-table-column label="Actions" width="300">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEditWorkflow(scope.row)">Edit</el-button>
            <el-button size="mini" type="primary" @click="handleCopyWorkflow(scope.row)">Copy</el-button>
            <el-button size="mini" type="warning" @click="handleDeployWorkflow(scope.row.id)">Deploy</el-button>
            <el-button size="mini" type="info" @click="handleSuspendWorkflow(scope.row.id)">Suspend</el-button>
            <el-button size="mini" type="danger" @click="handleDeleteWorkflow(scope.row.id)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- Preview XML Dialog -->
    <el-dialog title="BPMN XML Preview" :visible.sync="previewVisible" width="70%" fullscreen>
      <div class="xml-preview">
        <pre><code>{{ bpmnXml }}</code></pre>
      </div>
      <span slot="footer">
        <el-button type="primary" @click="handleCopyXml">Copy XML</el-button>
        <el-button @click="previewVisible = false">Close</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  listWorkflowDefinition,
  getWorkflowDefinition,
  createWorkflowDefinition,
  updateWorkflowDefinition,
  deleteWorkflowDefinition,
  deployWorkflow,
  suspendWorkflow
} from '@/api/nocode/workflow'

export default {
  name: 'WorkflowDesigner',
  data() {
    return {
      propertyTab: 'node',
      workflowConfig: {
        id: null,
        name: 'New Workflow',
        description: '',
        processKey: '',
        startFormKey: '',
        versionTag: '',
        historyLevel: 'audit',
        category: '',
        diagramJson: '',
        nodeDefinition: '',
        sequenceFlow: '',
        status: 'DRAFT',
        version: 1
      },
      nodes: [],
      edges: [],
      selectedNode: null,
      selectedEdge: null,
      saving: false,
      workflowListVisible: false,
      previewVisible: false,
      workflowList: [],
      currentWorkflow: { id: null },
      eventNodes: [
        { type: 'startEvent', label: 'Start Event', icon: 'el-icon-video-play', category: 'event' },
        { type: 'endEvent', label: 'End Event', icon: 'el-icon-circle-close', category: 'event' },
        { type: 'terminateEndEvent', label: 'Terminate End', icon: 'el-icon-circle-close', category: 'event' },
        { type: 'errorEndEvent', label: 'Error End', icon: 'el-icon-warning', category: 'event' }
      ],
      taskNodes: [
        { type: 'userTask', label: 'User Task', icon: 'el-icon-user', category: 'task' },
        { type: 'serviceTask', label: 'Service Task', icon: 'el-icon-setting', category: 'task' },
        { type: 'scriptTask', label: 'Script Task', icon: 'el-icon-document', category: 'task' },
        { type: 'receiveTask', label: 'Receive Task', icon: 'el-icon-chat-dot-square', category: 'task' },
        { type: 'manualTask', label: 'Manual Task', icon: 'el-icon-hand鼠标', category: 'task' },
        { type: 'businessRuleTask', label: 'Business Rule', icon: 'el-icon-s-data', category: 'task' }
      ],
      gatewayNodes: [
        { type: 'exclusiveGateway', label: 'Exclusive Gateway', icon: 'el-icon-d-arrow-right', category: 'gateway' },
        { type: 'parallelGateway', label: 'Parallel Gateway', icon: 'el-icon-finished', category: 'gateway' },
        { type: 'inclusiveGateway', label: 'Inclusive Gateway', icon: 'el-icon-s-operation', category: 'gateway' },
        { type: 'eventGateway', label: 'Event Gateway', icon: 'el-icon-bell', category: 'gateway' }
      ],
      connecting: false,
      connectionSource: null,
      tempEdge: null,
      draggingNode: null,
      dragOffset: { x: 0, y: 0 },
      zoom: 100,
      bpmnXml: ''
    }
  },
  computed: {
    nodeTypes() {
      return [...this.eventNodes, ...this.taskNodes, ...this.gatewayNodes]
    }
  },
  methods: {
    handleList() {
      listWorkflowDefinition().then(res => {
        if (res.code === 200) {
          this.workflowList = res.data
          this.workflowListVisible = true
        }
      }).catch(() => {
        this.workflowList = [
          { id: 1, name: 'Leave Request', processKey: 'leave_request', status: 'DEPLOYED', version: '1.2', description: 'Employee leave request workflow' },
          { id: 2, name: 'Purchase Approval', processKey: 'purchase_approval', status: 'DRAFT', version: '1.0', description: 'Purchase order approval workflow' }
        ]
        this.workflowListVisible = true
      })
    },
    handleNewWorkflow() {
      this.workflowConfig = {
        id: null,
        name: 'New Workflow',
        description: '',
        processKey: 'process_' + Date.now().toString(36),
        startFormKey: '',
        versionTag: '',
        historyLevel: 'audit',
        category: '',
        diagramJson: '',
        nodeDefinition: '',
        sequenceFlow: '',
        status: 'DRAFT',
        version: 1
      }
      this.nodes = []
      this.edges = []
      this.selectedNode = null
      this.selectedEdge = null
      this.currentWorkflow = { id: null }
      this.propertyTab = 'node'
    },
    handleSave() {
      if (!this.workflowConfig.name) {
        this.$message.warning('Please enter workflow name')
        return
      }
      if (this.nodes.length === 0) {
        this.$message.warning('Please add at least one node')
        return
      }
      this.saving = true
      this.workflowConfig.nodeDefinition = JSON.stringify(this.nodes)
      this.workflowConfig.sequenceFlow = JSON.stringify(this.edges)

      const savePromise = this.currentWorkflow.id
        ? updateWorkflowDefinition(this.currentWorkflow.id, this.workflowConfig)
        : createWorkflowDefinition(this.workflowConfig)

      savePromise.then(res => {
        if (res.code === 200) {
          this.$message.success('Workflow saved successfully')
          if (!this.currentWorkflow.id && res.data && res.data.id) {
            this.currentWorkflow = res.data
            this.workflowConfig.id = res.data.id
          }
        } else {
          this.$message.error(res.msg || 'Save failed')
        }
      }).finally(() => {
        this.saving = false
      })
    },
    handleDeploy() {
      if (!this.currentWorkflow.id) {
        this.$message.warning('Please save the workflow first')
        return
      }
      deployWorkflow(this.currentWorkflow.id).then(res => {
        if (res.code === 200) {
          this.$message.success('Workflow deployed successfully')
        } else {
          this.$message.error(res.msg || 'Deploy failed')
        }
      })
    },
    handlePreview() {
      this.generateBpmnXml()
      this.previewVisible = true
    },
    generateBpmnXml() {
      const processKey = this.workflowConfig.processKey || 'process'
      const processName = this.workflowConfig.name || 'Process'
      let xml = `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
                   xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
                   xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
                   xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
                   id="definitions_${processKey}"
                   targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:process id="${processKey}" name="${processName}" isExecutable="true">
    <bpmn:documentation>${this.workflowConfig.description || ''}</bpmn:documentation>
`
      // Add nodes
      this.nodes.forEach(node => {
        const nodeId = node.id.replace(/[^a-zA-Z0-9_]/g, '_')
        const nodeName = node.name || node.type
        if (node.type === 'startEvent') {
          xml += `    <bpmn:startEvent id="${nodeId}" name="${nodeName}">\n`
          if (node.eventType) {
            xml += `      <bpmn:${node.eventType}Definition/>\n`
          }
          xml += `    </bpmn:startEvent>\n`
        } else if (node.type === 'endEvent' || node.type === 'terminateEndEvent' || node.type === 'errorEndEvent') {
          xml += `    <bpmn:${node.type} id="${nodeId}" name="${nodeName}">\n`
          if (node.endType) {
            xml += `      <bpmn:${node.endType}EventDefinition/>\n`
          }
          xml += `    </bpmn:${node.type}>\n`
        } else if (['exclusiveGateway', 'parallelGateway', 'inclusiveGateway', 'eventGateway'].includes(node.type)) {
          const gatewayType = node.type === 'eventGateway' ? 'eventBasedGateway' : node.type
          xml += `    <bpmn:${gatewayType} id="${nodeId}" name="${nodeName}"/>\n`
        } else if (['userTask', 'serviceTask', 'scriptTask', 'receiveTask', 'manualTask', 'businessRuleTask'].includes(node.type)) {
          xml += `    <bpmn:${node.type} id="${nodeId}" name="${nodeName}"`
          if (node.type === 'userTask') {
            if (node.assignee) xml += ` bpmn:assignee="${node.assignee}"`
            if (node.formKey) xml += ` bpmn:formKey="${node.formKey}"`
          }
          if (node.type === 'serviceTask') {
            if (node.expression) xml += ` bpmn:expression="${node.expression}"`
            if (node.delegateExpression) xml += ` bpmn:delegateExpression="${node.delegateExpression}"`
          }
          if (node.type === 'scriptTask') {
            xml += ` bpmn:scriptFormat="${node.scriptFormat || 'javascript'}"`

          }
          xml += `>\n`
          if (node.script) {
            xml += `      <bpmn:script><![CDATA[${node.script}]]></bpmn:script>\n`
          }
          xml += `    </bpmn:${node.type}>\n`
        }
      })

      // Add sequence flows
      this.edges.forEach((edge, index) => {
        const edgeId = edge.id || `flow_${index + 1}`
        const sourceId = edge.source.replace(/[^a-zA-Z0-9_]/g, '_')
        const targetId = edge.target.replace(/[^a-zA-Z0-9_]/g, '_')
        xml += `    <bpmn:sequenceFlow id="${edgeId}" sourceRef="${sourceId}" targetRef="${targetId}"`
        if (edge.conditionType && edge.condition) {
          if (edge.conditionType === 'expression') {
            xml += `>\n      <bpmn:conditionExpression xsi:type="bpmn:tFormalExpression" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">${edge.condition}</bpmn:conditionExpression>\n    </bpmn:sequenceFlow>\n`
          } else {
            xml += `>\n      <bpmn:conditionExpression xsi:type="bpmn:tFormalExpression" language="${edge.conditionType}"><![CDATA[${edge.script || edge.condition}]]></bpmn:conditionExpression>\n    </bpmn:sequenceFlow>\n`
          }
        } else {
          xml += `/>\n`
        }
      })

      xml += `  </bpmn:process>\n</bpmn:definitions>`
      this.bpmnXml = xml
    },
    handleCopyXml() {
      this.$copyText(this.bpmnXml).then(() => {
        this.$message.success('XML copied to clipboard')
      })
    },
    onDragStart(event, nodeType) {
      event.dataTransfer.setData('nodeType', JSON.stringify(nodeType))
      event.dataTransfer.effectAllowed = 'copy'
    },
    onDragOver(event) {
      event.preventDefault()
      event.dataTransfer.dropEffect = 'copy'
    },
    onDrop(event) {
      event.preventDefault()
      const nodeTypeData = event.dataTransfer.getData('nodeType')
      const nodeType = nodeTypeData ? JSON.parse(nodeTypeData) : null
      if (nodeType) {
        const rect = this.$refs.canvas.getBoundingClientRect()
        const x = (event.clientX - rect.left) / (this.zoom / 100) - 40
        const y = (event.clientY - rect.top) / (this.zoom / 100) - 30
        const newNode = this.createNode(nodeType, x, y)
        this.nodes.push(newNode)
        this.selectNode(newNode)
      }
    },
    addNode(nodeType) {
      const x = 100 + Math.random() * 200
      const y = 100 + Math.random() * 200
      const newNode = this.createNode(nodeType, x, y)
      this.nodes.push(newNode)
      this.selectNode(newNode)
    },
    createNode(nodeType, x, y) {
      const node = {
        id: 'node_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9),
        type: nodeType.type,
        name: nodeType.label,
        x: x,
        y: y,
        error: false
      }
      // Add type-specific defaults
      if (nodeType.type === 'userTask') {
        node.assignee = ''
        node.candidateUsers = ''
        node.candidateGroups = ''
        node.formKey = ''
        node.dueDate = ''
        node.priority = 50
      }
      if (nodeType.type === 'serviceTask') {
        node.operationType = 'expression'
        node.expression = ''
        node.delegateExpression = ''
        node.className = ''
        node.resultVariable = ''
      }
      if (nodeType.type === 'scriptTask') {
        node.scriptFormat = 'javascript'
        node.script = ''
        node.resultVariable = ''
      }
      if (['exclusiveGateway', 'parallelGateway', 'inclusiveGateway'].includes(nodeType.type)) {
        node.gatewayDirection = 'diverging'
      }
      if (nodeType.type === 'startEvent') {
        node.eventType = ''
        node.timerValue = ''
      }
      if (nodeType.type === 'endEvent') {
        node.endType = ''
      }
      return node
    },
    selectNode(node) {
      this.selectedNode = node
      this.selectedEdge = null
      this.propertyTab = 'node'
    },
    selectEdge(edge, index) {
      this.selectedEdge = { ...edge, index }
      this.selectedNode = null
      this.propertyTab = 'flow'
    },
    onNodeMouseDown(event, node) {
      if (event.button === 0 && !event.target.classList.contains('connection-point')) {
        this.draggingNode = node
        this.dragOffset = {
          x: event.clientX - node.x,
          y: event.clientY - node.y
        }
        document.addEventListener('mousemove', this.onNodeMouseMove)
        document.addEventListener('mouseup', this.onNodeMouseUp)
      }
    },
    onNodeMouseMove(event) {
      if (this.draggingNode) {
        const canvasRect = this.$refs.canvas.getBoundingClientRect()
        this.draggingNode.x = (event.clientX - this.dragOffset.x - canvasRect.left) / (this.zoom / 100)
        this.draggingNode.y = (event.clientY - this.dragOffset.y - canvasRect.top) / (this.zoom / 100)
        // Ensure minimum position
        this.draggingNode.x = Math.max(0, this.draggingNode.x)
        this.draggingNode.y = Math.max(0, this.draggingNode.y)
      }
    },
    onNodeMouseUp() {
      this.draggingNode = null
      document.removeEventListener('mousemove', this.onNodeMouseMove)
      document.removeEventListener('mouseup', this.onNodeMouseUp)
    },
    onNodeDoubleClick(node) {
      this.$prompt('Enter node name:', 'Rename Node', {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        inputValue: node.name
      }).then(({ value }) => {
        node.name = value
        this.$message.success('Node renamed')
      }).catch(() => {})
    },
    onConnectionStart(node, type) {
      if (type === 'target') {
        this.connecting = true
        this.connectionSource = node
        this.tempEdge = ''
      }
    },
    onConnectionEnd(node, type) {
      if (this.connecting && this.connectionSource && this.connectionSource.id !== node.id) {
        // Check if edge already exists
        const exists = this.edges.some(e => e.source === this.connectionSource.id && e.target === node.id)
        if (!exists) {
          this.edges.push({
            id: 'flow_' + Date.now(),
            source: this.connectionSource.id,
            target: node.id,
            conditionType: '',
            condition: '',
            label: ''
          })
        }
      }
      this.connecting = false
      this.connectionSource = null
      this.tempEdge = null
    },
    onCanvasMouseMove(event) {
      if (this.connecting && this.connectionSource) {
        const rect = this.$refs.canvas.getBoundingClientRect()
        const startX = this.connectionSource.x + 40
        const startY = this.connectionSource.y + 30
        const endX = (event.clientX - rect.left) / (this.zoom / 100)
        const endY = (event.clientY - rect.top) / (this.zoom / 100)
        this.tempEdge = `M ${startX} ${startY} L ${endX} ${endY}`
      }
    },
    onCanvasMouseUp() {
      this.connecting = false
      this.connectionSource = null
      this.tempEdge = null
    },
    deleteSelectedNode() {
      if (!this.selectedNode) return
      this.$confirm('Are you sure to delete this node?').then(() => {
        const nodeId = this.selectedNode.id
        this.nodes = this.nodes.filter(n => n.id !== nodeId)
        this.edges = this.edges.filter(e => e.source !== nodeId && e.target !== nodeId)
        this.selectedNode = null
      }).catch(() => {})
    },
    clearAll() {
      this.$confirm('Are you sure to clear all nodes and connections?').then(() => {
        this.nodes = []
        this.edges = []
        this.selectedNode = null
        this.selectedEdge = null
      }).catch(() => {})
    },
    zoomIn() {
      this.zoom = Math.min(200, this.zoom + 10)
    },
    zoomOut() {
      this.zoom = Math.max(50, this.zoom - 10)
    },
    zoomReset() {
      this.zoom = 100
    },
    autoLayout() {
      // Simple auto layout - arrange nodes in a grid
      const cols = Math.ceil(Math.sqrt(this.nodes.length))
      const nodeWidth = 120
      const nodeHeight = 80
      const padding = 60

      this.nodes.forEach((node, index) => {
        const col = index % cols
        const row = Math.floor(index / cols)
        node.x = padding + col * (nodeWidth + padding)
        node.y = padding + row * (nodeHeight + padding)
      })
    },
    getNodeIcon(type) {
      const iconMap = {
        startEvent: 'el-icon-video-play',
        endEvent: 'el-icon-circle-close',
        terminateEndEvent: 'el-icon-close',
        errorEndEvent: 'el-icon-warning',
        userTask: 'el-icon-user',
        serviceTask: 'el-icon-setting',
        scriptTask: 'el-icon-document',
        receiveTask: 'el-icon-chat-dot-square',
        manualTask: 'el-icon-hand-pointer',
        businessRuleTask: 'el-icon-s-data',
        exclusiveGateway: 'el-icon-d-arrow-right',
        parallelGateway: 'el-icon-finished',
        inclusiveGateway: 'el-icon-s-operation',
        eventGateway: 'el-icon-bell'
      }
      return iconMap[type] || 'el-icon-circle'
    },
    getNodeTypeLabel(type) {
      const labelMap = {
        startEvent: 'Start',
        endEvent: 'End',
        terminateEndEvent: 'Terminate',
        errorEndEvent: 'Error End',
        userTask: 'User Task',
        serviceTask: 'Service',
        scriptTask: 'Script',
        receiveTask: 'Receive',
        manualTask: 'Manual',
        businessRuleTask: 'Business',
        exclusiveGateway: 'X Gateway',
        parallelGateway: 'Parallel',
        inclusiveGateway: 'Inclusive',
        eventGateway: 'Event'
      }
      return labelMap[type] || type
    },
    getEdgePath(edge) {
      const sourceNode = this.nodes.find(n => n.id === edge.source)
      const targetNode = this.nodes.find(n => n.id === edge.target)
      if (!sourceNode || !targetNode) return ''

      const sx = sourceNode.x + 40
      const sy = sourceNode.y + 30
      const tx = targetNode.x + 40
      const ty = targetNode.y + 30

      // Calculate control points for curved line
      const dx = tx - sx
      const dy = ty - sy
      const midX = (sx + tx) / 2
      const midY = (sy + ty) / 2

      // Simple curved path using quadratic bezier
      const cx = midX
      const cy = midY - Math.abs(dx) * 0.2

      return `M ${sx} ${sy} Q ${cx} ${cy} ${tx} ${ty}`
    },
    getEdgeMidPoint(edge) {
      const sourceNode = this.nodes.find(n => n.id === edge.source)
      const targetNode = this.nodes.find(n => n.id === edge.target)
      if (!sourceNode || !targetNode) return { x: 0, y: 0 }
      return {
        x: (sourceNode.x + targetNode.x) / 2 + 40,
        y: (sourceNode.y + targetNode.y) / 2 + 30
      }
    },
    getOutgoingEdges(nodeId) {
      return this.edges.filter(e => e.source === nodeId)
    },
    getStatusType(status) {
      const typeMap = {
        DRAFT: 'info',
        DEPLOYED: 'success',
        SUSPENDED: 'warning'
      }
      return typeMap[status] || 'info'
    },
    handleEditWorkflow(row) {
      getWorkflowDefinition(row.id).then(res => {
        if (res.code === 200) {
          this.workflowConfig = { ...this.workflowConfig, ...res.data }
          this.nodes = res.data.nodeDefinition ? JSON.parse(res.data.nodeDefinition) : []
          this.edges = res.data.sequenceFlow ? JSON.parse(res.data.sequenceFlow) : []
          this.currentWorkflow = row
          this.workflowListVisible = false
        }
      }).catch(() => {
        this.workflowConfig = {
          id: row.id,
          name: row.name,
          processKey: row.processKey,
          description: row.description,
          status: row.status,
          version: row.version
        }
        this.nodes = [
          { id: 'start', type: 'startEvent', name: 'Start', x: 100, y: 100 },
          { id: 'task1', type: 'userTask', name: 'Approve', x: 300, y: 100, assignee: '' },
          { id: 'end', type: 'endEvent', name: 'End', x: 500, y: 100 }
        ]
        this.edges = [
          { id: 'flow1', source: 'start', target: 'task1' },
          { id: 'flow2', source: 'task1', target: 'end' }
        ]
        this.currentWorkflow = row
        this.workflowListVisible = false
      })
    },
    handleCopyWorkflow(row) {
      this.handleEditWorkflow(row)
      this.workflowConfig.id = null
      this.workflowConfig.name = row.name + ' (Copy)'
      this.workflowConfig.processKey = row.processKey + '_copy'
      this.currentWorkflow = { id: null }
      this.$message.info('Modify the copied workflow and save as new')
    },
    handleDeployWorkflow(id) {
      deployWorkflow(id).then(res => {
        if (res.code === 200) {
          this.$message.success('Workflow deployed successfully')
          this.handleList()
        } else {
          this.$message.error(res.msg || 'Deploy failed')
        }
      })
    },
    handleSuspendWorkflow(id) {
      suspendWorkflow(id).then(res => {
        if (res.code === 200) {
          this.$message.success('Workflow suspended')
          this.handleList()
        }
      })
    },
    handleDeleteWorkflow(id) {
      this.$confirm('Are you sure to delete this workflow?').then(() => {
        deleteWorkflowDefinition(id).then(res => {
          if (res.code === 200) {
            this.$message.success('Workflow deleted')
            this.handleList()
          }
        })
      })
    }
  },
  mounted() {
    const workflowId = this.$route.query.id
    if (workflowId) {
      this.handleEditWorkflow({ id: workflowId })
    }
  }
}
</script>

<style scoped>
.workflow-designer-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}

.workflow-designer-header {
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.workflow-designer-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.workflow-designer-main {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.node-library {
  width: 180px;
  background: #fff;
  border-right: 1px solid #e8e8e8;
  overflow-y: auto;
}

.library-section {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.library-section:last-child {
  border-bottom: none;
}

.library-section h3 {
  margin: 0 0 10px 0;
  font-size: 11px;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.node-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.node-item {
  padding: 8px 10px;
  background: #f5f7fa;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  cursor: move;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
  font-size: 12px;
}

.node-item:hover {
  border-color: #667eea;
  background: #f0f2ff;
  color: #667eea;
}

.node-item i {
  font-size: 14px;
  color: #666;
}

.node-item:hover i {
  color: #667eea;
}

.workflow-canvas {
  flex: 1;
  overflow: auto;
  background: #e8e8e8;
  position: relative;
}

.canvas-toolbar {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 10;
  background: #fff;
  padding: 8px;
  border-radius: 6px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.canvas-content {
  position: relative;
  width: 3000px;
  height: 2000px;
  background: #fff;
  transform-origin: 0 0;
  transition: transform 0.2s;
}

.edge-svg {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: visible;
}

.edge-path {
  pointer-events: stroke;
  cursor: pointer;
}

.edge-label {
  pointer-events: click;
  cursor: pointer;
  font-size: 12px;
  fill: #666;
}

.workflow-node {
  position: absolute;
  width: 120px;
  height: 80px;
  background: #fff;
  border: 2px solid #dcdfe6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  user-select: none;
  transition: box-shadow 0.2s, border-color 0.2s;
}

.workflow-node:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.workflow-node.selected {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
}

.workflow-node.has-error {
  border-color: #f56c6c;
}

/* Start Event */
.workflow-node.startEvent {
  border-radius: 50%;
  width: 60px;
  height: 60px;
  background: #67c23a;
  border-color: #67c23a;
  color: #fff;
}

/* End Events */
.workflow-node.endEvent,
.workflow-node.terminateEndEvent,
.workflow-node.errorEndEvent {
  border-radius: 50%;
  width: 60px;
  height: 60px;
  background: #f56c6c;
  border-color: #f56c6c;
  color: #fff;
}

.workflow-node.terminateEndEvent {
  background: #909399;
  border-color: #909399;
}

.workflow-node.errorEndEvent {
  background: #e6a23c;
  border-color: #e6a23c;
}

/* Gateway */
.workflow-node.exclusiveGateway,
.workflow-node.parallelGateway,
.workflow-node.inclusiveGateway,
.workflow-node.eventGateway {
  border-radius: 0;
  width: 80px;
  height: 80px;
  background: #e6a23c;
  border-color: #e6a23c;
  color: #fff;
  transform: rotate(45deg);
}

.workflow-node.exclusiveGateway > *,
.workflow-node.parallelGateway > *,
.workflow-node.inclusiveGateway > *,
.workflow-node.eventGateway > * {
  transform: rotate(-45deg);
}

/* User Task */
.workflow-node.userTask {
  border-color: #409eff;
}

/* Service Task */
.workflow-node.serviceTask {
  border-color: #909399;
}

/* Script Task */
.workflow-node.scriptTask {
  border-color: #67c23a;
}

/* Receive Task */
.workflow-node.receiveTask {
  border-color: #f56c6c;
}

/* Manual Task */
.workflow-node.manualTask {
  border-color: #e6a23c;
}

/* Business Rule Task */
.workflow-node.businessRuleTask {
  border-color: #9234ea;
}

.node-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.node-label {
  font-size: 12px;
  font-weight: 500;
  color: #303133;
  text-align: center;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.workflow-node.startEvent .node-label,
.workflow-node.endEvent .node-label,
.workflow-node.terminateEndEvent .node-label,
.workflow-node.errorEndEvent .node-label {
  color: #fff;
  font-size: 10px;
}

.node-type-badge {
  font-size: 9px;
  color: #909399;
  margin-top: 2px;
}

.workflow-node.startEvent .node-type-badge,
.workflow-node.endEvent .node-type-badge,
.workflow-node.terminateEndEvent .node-type-badge,
.workflow-node.errorEndEvent .node-type-badge {
  color: rgba(255, 255, 255, 0.8);
}

.node-error {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 20px;
  height: 20px;
  background: #f56c6c;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 12px;
}

.connection-point {
  position: absolute;
  width: 12px;
  height: 12px;
  background: #fff;
  border: 2px solid #667eea;
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.2s;
}

.workflow-node:hover .connection-point {
  opacity: 1;
}

.connection-point.source {
  right: -6px;
  top: 50%;
  transform: translateY(-50%);
  cursor: crosshair;
}

.connection-point.target {
  left: -6px;
  top: 50%;
  transform: translateY(-50%);
  cursor: crosshair;
}

.temp-edge {
  position: absolute;
  pointer-events: none;
}

.properties-panel {
  width: 340px;
  background: #fff;
  border-left: 1px solid #e8e8e8;
  overflow-y: auto;
}

.property-form {
  padding: 8px;
}

.no-selection {
  color: #909399;
  text-align: center;
  padding: 60px 20px;
}

.no-selection i {
  font-size: 48px;
  color: #dcdfe6;
  margin-bottom: 16px;
}

.no-selection p {
  margin: 0;
  font-size: 14px;
}

.no-selection .hint {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 8px;
}

.xml-preview {
  background: #1e1e1e;
  padding: 20px;
  border-radius: 4px;
  max-height: 60vh;
  overflow: auto;
}

.xml-preview pre {
  margin: 0;
  color: #d4d4d4;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  line-height: 1.5;
}
</style>