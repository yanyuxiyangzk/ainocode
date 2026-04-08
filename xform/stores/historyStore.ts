import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { FormSchema } from '@/types/form-schema';

export const useHistoryStore = defineStore('history', () => {
  const undoStack = ref<FormSchema[]>([]);
  const redoStack = ref<FormSchema[]>([]);
  const maxHistorySize = 50;

  const canUndo = computed(() => undoStack.value.length > 0);
  const canRedo = computed(() => redoStack.value.length > 0);

  function pushState(schema: FormSchema) {
    undoStack.value.push(JSON.parse(JSON.stringify(schema)));
    if (undoStack.value.length > maxHistorySize) {
      undoStack.value.shift();
    }
    redoStack.value = [];
  }

  function undo(): FormSchema | null {
    if (!canUndo.value) return null;
    const currentState = redoStack.value.pop();
    if (currentState) {
      undoStack.value.push(currentState);
    }
    return undoStack.value.pop() ?? null;
  }

  function redo(): FormSchema | null {
    if (!canRedo.value) return null;
    const state = redoStack.value.pop()!;
    undoStack.value.push(JSON.parse(JSON.stringify(state)));
    return state;
  }

  function clear() {
    undoStack.value = [];
    redoStack.value = [];
  }

  return { canUndo, canRedo, pushState, undo, redo, clear };
});
