{{/*
Expand the name of the chart.
*/}}
{{- define "ruoyi-nocode.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "ruoyi-nocode.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create the service account name
*/}}
{{- define "ruoyi-nocode.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "ruoyi-nocode.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create chart name and version
*/}}
{{- define "ruoyi-nocode.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "ruoyi-nocode.labels" -}}
app.kubernetes.io/name: {{ include "ruoyi-nocode.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Values.image.tag | default .Chart.AppVersion }}
app.kubernetes.io/component: {{ .Chart.Name }}
app.kubernetes.io/part-of: {{ include "ruoyi-nocode.name" . }}
helm.sh/chart: {{ include "ruoyi-nocode.chart" . }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "ruoyi-nocode.selectorLabels" -}}
app.kubernetes.io/name: {{ include "ruoyi-nocode.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
