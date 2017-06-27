# MyZxingLib
一个基于zxing的扫码库，功能暂未完善
# 使用
  Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
  startActivityForResult(intent, 0x11);
  
  
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            Log.i("yqy", "扫描返回的数据" + data.getStringExtra("result"));
        }
    }
