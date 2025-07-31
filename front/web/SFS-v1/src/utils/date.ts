/**
 * 日期格式化工具
 * 提供常用的日期时间格式化功能
 */

/**
 * 格式化日期时间字符串
 * @param dateString - 原始日期字符串
 * @param format - 目标格式，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns 格式化后的日期字符串
 */
export function formatTime(dateString: string, format: string = 'YYYY-MM-DD HH:mm:ss'): string {
  const date = new Date(dateString);

  // 处理无效日期
  if (isNaN(date.getTime())) {
    console.error('Invalid date string:', dateString);
    return dateString;
  }

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');

  // 替换格式占位符
  return format
    .replace('YYYY', year.toString())
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
}

/**
 * 计算两个日期之间的时间差（秒）
 * @param startDate - 开始日期
 * @param endDate - 结束日期
 * @returns 时间差（秒）
 */
export function getTimeDifferenceInSeconds(startDate: string, endDate: string): number {
  const start = new Date(startDate).getTime();
  const end = new Date(endDate).getTime();
  return Math.floor((end - start) / 1000);
}

/**
 * 格式化时长（秒转时分秒）
 * @param seconds - 总秒数
 * @returns 格式化后的时长字符串
 */
export function formatDuration(seconds: number): string {
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const remainingSeconds = seconds % 60;

  const parts: string[] = [];
  if (hours > 0) {
    parts.push(`${hours}时`);
  }
  if (minutes > 0 || hours > 0) {
    parts.push(`${minutes}分`);
  }
  parts.push(`${remainingSeconds}秒`);

  return parts.join('');
}