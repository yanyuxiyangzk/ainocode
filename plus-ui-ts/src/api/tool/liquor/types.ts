/**
 * Liquor IDE Types
 */

/**
 * Java source code compilation request
 */
export interface LiquorCompileForm {
  /** Java source code */
  sourceCode: string;
  /** Class name to compile */
  className: string;
}

/**
 * Compilation result
 */
export interface LiquorCompileVO {
  /** Success flag */
  success: boolean;
  /** Compiled class name */
  className: string;
  /** Compiled class bytecode URL (optional) */
  bytecodeUrl?: string;
  /** Error message (if failed) */
  errorMessage?: string | string[];
  /** Errors array (if failed) */
  errors?: string[];
  /** Compilation time in ms */
  compileTime?: number;
}

/**
 * Hot swap request
 */
export interface LiquorHotSwapForm {
  /** Java source code */
  sourceCode: string;
  /** Class name to hot swap */
  className: string;
  /** Domain for isolation */
  domain: string;
}

/**
 * Hot swap result
 */
export interface LiquorHotSwapVO {
  /** Success flag */
  success: boolean;
  /** New version number */
  version: number;
  /** Swap ID */
  swapId: string;
  /** ClassLoader ID */
  classLoaderId: string;
  /** Error message (if failed) */
  errorMessage?: string;
}

/**
 * Sandbox execution request
 */
export interface LiquorExecuteForm {
  /** Class name to execute */
  className: string;
  /** Method name to call (default: main) */
  methodName?: string;
  /** Method parameters (JSON array string) */
  methodParams?: string;
  /** Execution timeout in milliseconds */
  timeout?: number;
  /** Enable sandbox (default: true) */
  sandboxEnabled?: boolean;
}

/**
 * Sandbox execution result
 */
export interface LiquorExecuteVO {
  /** Success flag */
  success: boolean;
  /** Execution result (serialized) */
  result?: any;
  /** Result type */
  resultType?: string;
  /** Execution time in ms */
  executionTime?: number;
  /** Error message (if failed) */
  errorMessage?: string;
  /** Error type */
  errorType?: string;
  /** Execution logs */
  logs?: string[];
}

/**
 * Liquor domain info
 */
export interface LiquorDomainVO {
  /** Domain name */
  domain: string;
  /** Current version */
  currentVersion: number;
  /** Latest version */
  latestVersion: number;
  /** ClassLoader ID */
  classLoaderId: string;
  /** Loaded classes count */
  classCount: number;
}

/**
 * Query params for domain list
 */
export interface LiquorDomainQuery extends PageQuery {
  /** Domain name (optional) */
  domain?: string;
}
