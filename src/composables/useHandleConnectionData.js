/**
 * 使用 Web Message Listener 处理从 app 发送的数据
 *
 * @param {Function} cb - 处理 app 传送回来的数据的回调函数，cb 只接收一个参数 event，app 的回传数据会以 string 形式存在 event.data 中
 *
 * <strong>重要提醒：</strong>建议只在 /views 调用此函数，每个 view 只调用一次，避免在 /components 层调用，以免创建重复监听器。
 */
function useHandleConnectionData(cb) {
  if (typeof flutterObject !== 'undefined' && flutterObject) {
    if (cb) {
      flutterObject.addEventListener('message', cb);

      // Clean up the event listener when the page or component is unmounted
      window.addEventListener('beforeunload', () => {
        flutterObject.removeEventListener('message', cb);
      });
    }
  }
}