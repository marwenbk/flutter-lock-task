import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_lock_task/flutter_lock_task.dart';
import 'package:flutter_lock_task/flutter_lock_task_platform_interface.dart';
import 'package:flutter_lock_task/flutter_lock_task_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterLockTaskPlatform
    with MockPlatformInterfaceMixin
    implements FlutterLockTaskPlatform {
  @override
  Future<String?> getPackageName() => Future.value('');

  @override
  Future<bool> clearDeviceOwnerApp() {
    // TODO: implement clearDeviceOwnerApp
    throw UnimplementedError();
  }

  @override
  Future<bool> isInLockTaskMode() {
    // TODO: implement isInLockTaskMode
    throw UnimplementedError();
  }

  @override
  Future<bool> openHomeSettings() {
    // TODO: implement openHomeSettings
    throw UnimplementedError();
  }

  @override
  Future<bool> setDeviceOwnerApp() {
    // TODO: implement setDeviceOwnerApp
    throw UnimplementedError();
  }

  @override
  Future<bool> startLockTask() {
    // TODO: implement startLockTask
    throw UnimplementedError();
  }

  @override
  Future<bool> stopLockTask() {
    // TODO: implement stopLockTask
    throw UnimplementedError();
  }
}

void main() {
  final FlutterLockTaskPlatform initialPlatform =
      FlutterLockTaskPlatform.instance;

  test('$MethodChannelFlutterLockTask is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterLockTask>());
  });

  test('getPlatformVersion', () async {
    FlutterLockTask flutterLockTaskPlugin = FlutterLockTask();
    MockFlutterLockTaskPlatform fakePlatform = MockFlutterLockTaskPlatform();
    FlutterLockTaskPlatform.instance = fakePlatform;

    expect(await flutterLockTaskPlugin.getPackageName(), '');
  });
}
