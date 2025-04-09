import 'dart:async';
import 'dart:developer';
import 'dart:typed_data';

import 'package:danger_sound_recognition/src/features/record/application/audio_classifier_service.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart' show MethodChannel, PlatformException;
import '/l10n/generated/app_localizations.dart';
import '../../../common_widgets/settings_drawer.dart';
import '../../../routing/route_names.dart';

class RecordPage extends StatefulWidget {
  const RecordPage({super.key});

  @override
  State<RecordPage> createState() => _RecordPageState();
}

class _RecordPageState extends State<RecordPage> {
  // GlobalKey to manage Scaffold
  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();
  static const platform =
    MethodChannel('org.tensorflow.audio_classification/audio_record');
 static const _sampleRate = 16000; // 16kHz
  static const _expectAudioLength = 975; // milliseconds
  final int _requiredInputBuffer =
      (16000 * (_expectAudioLength / 1000)).toInt();
  late AudioClassifierService _classifier;
  List<MapEntry<String, double>> _classification = List.empty();
  final List<Color> _primaryProgressColorList = [
    const Color(0xFFF44336),
    const Color(0xFFE91E63),
    const Color(0xFF9C27B0),
    const Color(0xFF3F51B5),
    const Color(0xFF2196F3),
    const Color(0xFF00BCD4),
    const Color(0xFF009688),
    const Color(0xFF4CAF50),
    const Color(0xFFFFEB3B),
    const Color(0xFFFFC107),
    const Color(0xFFFF9800)
  ];
  final List<Color> _backgroundProgressColorList = [
    const Color(0x44F44336),
    const Color(0x44E91E63),
    const Color(0x449C27B0),
    const Color(0x443F51B5),
    const Color(0x442196F3),
    const Color(0x4400BCD4),
    const Color(0x44009688),
    const Color(0x444CAF50),
    const Color(0x44FFEB3B),
    const Color(0x44FFC107),
    const Color(0x44FF9800)
  ];
  var _showError = false;

  void _startRecorder() {
    try {
      platform.invokeMethod('startRecord');
    } on PlatformException catch (e) {
      log("Failed to start record: '${e.message}'.");
    }
  }

  Future<bool> _requestPermission() async {
    try {
      return await platform.invokeMethod('requestPermissionAndCreateRecorder', {
        "sampleRate": _sampleRate,
        "requiredInputBuffer": _requiredInputBuffer
      });
    } on Exception catch (e) {
      log("Failed to create recorder: '${e.toString()}'.");
      return false;
    }
  }

  Future<Float32List> _getAudioFloatArray() async {
    var audioFloatArray = Float32List(0);
    try {
      final Float32List result =
          await platform.invokeMethod('getAudioFloatArray');
      audioFloatArray = result;
    } on PlatformException catch (e) {
      log("Failed to get audio array: '${e.message}'.");
    }
    return audioFloatArray;
  }

  Future<void> _closeRecorder() async {
    try {
      await platform.invokeMethod('closeRecorder');
      _classifier.closeInterpreter();
    } on PlatformException {
      log("Failed to close recorder.");
    }
  }

  @override
  initState() {
    _initRecorder();
    super.initState();
  }

  Future<void> _initRecorder() async {
    _classifier = AudioClassifierService();
    await _classifier.initHelper();
    bool success = await _requestPermission();
    if (success) {
      _startRecorder();

      Timer.periodic(const Duration(milliseconds: _expectAudioLength), (timer) {
        // classify here
        _runInference();
      });
    } else {
      // show error here
      setState(() {
        _showError = false;
      });
    }
  }

  Future<void> _runInference() async {
    Float32List inputArray = await _getAudioFloatArray();
    final result =
        await _classifier.inference(inputArray.sublist(0, _requiredInputBuffer));
    setState(() {
      // take top 3 classification
      _classification = (result.entries.toList()
            ..sort(
              (a, b) => a.value.compareTo(b.value),
            ))
          .reversed
          .take(3)
          .toList();
    });
  }

  @override
  void dispose() {
    _closeRecorder();
    super.dispose();
  }

  Widget _buildClassifierFunctionBody() {
  if (_showError) {
    return const Center(
      child: Text(
        "Audio recording permission required for audio classification",
        textAlign: TextAlign.center,
      ),
    );
  } else {
    return ListView.separated(
      padding: const EdgeInsets.all(10),
      physics: const BouncingScrollPhysics(),
      shrinkWrap: true,
      itemCount: _classification.length,
      itemBuilder: (context, index) {
        final item = _classification[index];
        return Row(
          children: [
            SizedBox(
              width: 200,
              child: Text(item.key),
            ),
            Flexible(
                child: LinearProgressIndicator(
              backgroundColor: _backgroundProgressColorList[
                  index % _backgroundProgressColorList.length],
              color: _primaryProgressColorList[
                  index % _primaryProgressColorList.length],
              value: item.value,
              minHeight: 20,
            ))
          ],
        );
      },
      separatorBuilder: (BuildContext context, int index) => const SizedBox(
        height: 10,
      ),
    );
  }
}


  @override
  Widget build(BuildContext context) {
    final localizations = AppLocalizations.of(context)!;

    return Scaffold(
      key: _scaffoldKey, // Assigning the GlobalKey here
      appBar: AppBar(
        title: Text(localizations.appBar),
        leading: IconButton(
          icon: const Icon(Icons.menu), // Open Main Drawer
          onPressed: () {
            _scaffoldKey.currentState?.openDrawer(); // Open LEFT Drawer
          },
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings), // Open Settings Drawer
            onPressed: () {
              _scaffoldKey.currentState?.openEndDrawer(); // Open RIGHT Drawer
            },
          ),
        ],
      ),
      drawer: const MainDrawer(), // Main Navigation Drawer (left)
      endDrawer: const SettingsDrawer(), // Settings Drawer (right)
      // body: Center(
      //   child: Column(
      //     mainAxisAlignment: MainAxisAlignment.center,
      //     children: [
      //       Text('Main Content Here'),
      //       ElevatedButton(
      //         onPressed: () {
      //           Navigator.pushNamed(context, RouteNames.classificationConfig);
      //           print('Navigating to ClassificationConfig'); // Debug print
      //         },
      //         child: const Text('Classification Settings'),
      //       ),
      //     ],
      //   ),
      // ),
      body: Center(
        child: _buildClassifierFunctionBody(),
      ),
    );
  }
}

class MainDrawer extends StatelessWidget {
  const MainDrawer({super.key});

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    return Drawer(
      child: ListView(
        children: [
          DrawerHeader(
            decoration: const BoxDecoration(color: Colors.blue),
            child: Text(
              'Main Navigation',
              style: const TextStyle(color: Colors.white, fontSize: 24),
            ),
          ),
          ListTile(
            leading: const Icon(Icons.home),
            title: const Text('Home'),
            onTap: () {
              Navigator.pop(context);
              Navigator.pushReplacementNamed(context, RouteNames.record);
            },
          ),
          ListTile(
            leading: const Icon(Icons.history),
            title: const Text('Records History'), // Using hardcoded string
            onTap: () {
              Navigator.pop(context);
              Navigator.pushNamed(context, RouteNames.recordsHistory);
            },
          ),
          // Add Classification Config navigation option
          ListTile(
            leading: const Icon(Icons.tune),
            title: const Text('Classification Settings'),
            onTap: () {
              Navigator.pop(context);
              print(
                'Navigating to ClassificationConfig from drawer',
              ); // Debug print
              Navigator.pushNamed(context, RouteNames.classificationConfig);
            },
          ),
        ],
      ),
    );
  }
}
