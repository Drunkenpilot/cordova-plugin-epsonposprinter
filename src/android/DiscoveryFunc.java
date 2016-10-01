
  private DiscoveryListener mDiscoveryListener = new DiscoveryListener( ) {
    @Override
    public void onDiscovery(final DeviceInfo deviceInfo) {
      Log.i("测试", "测试5");
      HashMap<String, String> item = new HashMap<String, String>();
      item.put("PrinterName", deviceInfo.getDeviceName());
      item.put("Target", deviceInfo.getTarget());
      Log.i("测试", "PrinterName: " + deviceInfo.getDeviceName() + "; " + "Target: " + deviceInfo.getTarget());

      mPrinterList.add(item);
      for (HashMap<String, String> one : mPrinterList) {
        Log.i("测试", "mPrinterList: " + one.get("PrinterName") + " ~ " + one.get("Target"));
      }
      Log.i("测试", "测试6");
      this.showToast(deviceInfo);
      // mPrinterListAdapter.notifyDataSetChanged();
      // return item;
      Log.i("测试", "测试7");
      Log.i("测试", "测试8");
    }

    public void showToast(final DeviceInfo deviceInfo) {
      cordova.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          Toast.makeText(cordova.getActivity(), "PrinterName: " + deviceInfo.getDeviceName(),
          Toast.LENGTH_SHORT).show();
          Toast.makeText(cordova.getActivity(), "Target: " + deviceInfo.getTarget(), Toast.LENGTH_SHORT)
          .show();
        }
      });
    }

  };
