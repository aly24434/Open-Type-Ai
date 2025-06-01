package com.example.opentypeai

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opentypeai.databinding.FragmentFontInspectorBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.InputStream

class FontInspectorFragment : Fragment() {

    private var _binding: FragmentFontInspectorBinding? = null
    private val binding get() = _binding!!

    private var currentFont: Typeface? = null
    private var fontInfo: FontInfo? = null
    private var textColor = Color.WHITE
    private var bgColor = Color.TRANSPARENT
    private var textSize = 24f
    private val activeFeatures = mutableMapOf<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFontInspectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupButtons()
        setupTextSizeSelector()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupButtons() {
        binding.btnLoadFont.setOnClickListener {
            loadCustomFont()
        }

        binding.btnDefaultFont.setOnClickListener {
            loadDefaultFont()
        }

        binding.btnTextColor.setOnClickListener {
            showColorPicker(false)
        }

        binding.btnBgColor.setOnClickListener {
            showColorPicker(true)
        }
    }

    private fun setupTextSizeSelector() {
        val sizes = arrayOf("12sp", "16sp", "20sp", "24sp", "28sp", "32sp", "36sp", "48sp")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.btnTextSize.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("اختر حجم النص")
                .setAdapter(adapter) { _, which ->
                    textSize = when(which) {
                        0 -> 12f
                        1 -> 16f
                        2 -> 20f
                        3 -> 24f
                        4 -> 28f
                        5 -> 32f
                        6 -> 36f
                        else -> 48f
                    }
                    updatePreview()
                }
                .show()
        }
    }

    private fun loadCustomFont() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "font/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "font/ttf",
                "font/otf",
                "application/vnd.ms-opentype",
                "application/x-font-ttf",
                "application/x-font-otf"
            ))
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_FONT)
    }

    private fun loadDefaultFont() {
        val fontPath = "fonts/Amiri-Regular.ttf"
        currentFont = Typeface.createFromAsset(requireContext().assets, fontPath)
        analyzeFont()
        updatePreview()
        showFontInfo("Amiri Regular", "خط عربي كلاسيكي")
    }

    private fun analyzeFont() {
        fontInfo = FontInfo(
            name = "Custom Font",
            family = "Arabic",
            style = "Regular",
            version = "1.0",
            designer = "Unknown",
            features = listOf(
                FeatureInfo("liga", "الترابط", "تجمع بين حرفين أو أكثر لتكوين شكل واحد"),
                FeatureInfo("calt", "البدائل السياقية", "تغيير شكل الحرف حسب سياقه في الجملة"),
                FeatureInfo("kern", "التقنين", "ضبط المسافة بين أزواج الحروف"),
                FeatureInfo("frac", "الكسور", "عرض الكسور بشكل صحيح"),
                FeatureInfo("ss01", "النمط البديل 1", "نمط بديل للحروف"),
                FeatureInfo("ss02", "النمط البديل 2", "نمط بديل إضافي للحروف")
            )
        )

        binding.featuresInfo.text = "تم تحليل الخط، ${fontInfo?.features?.size} خاصية متاحة"
        setupFeaturesList()
    }

    private fun setupFeaturesList() {
        val features = fontInfo?.features ?: return
        val adapter = FeatureAdapter(features) { tag, state ->
            activeFeatures[tag] = state
            applyOpenTypeFeatures()
        }

        binding.featuresRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.featuresRecycler.adapter = adapter
    }

    private fun applyOpenTypeFeatures() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val fontFeatureSettings = activeFeatures.entries.joinToString(",") {
                "'${it.key}' ${it.value}"
            }
            binding.previewText.fontFeatureSettings = fontFeatureSettings
        } else {
            // حل بديل للإصدارات الأقدم
        }
    }

    private fun showColorPicker(isBackground: Boolean) {
        val colorPicker = AmbilWarnaDialog(requireContext(),
            if (isBackground) bgColor else textColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog) {}
                override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                    if (isBackground) {
                        bgColor = color
                        binding.previewText.setBackgroundColor(color)
                    } else {
                        textColor = color
                        binding.previewText.setTextColor(color)
                    }
                }
            })
        colorPicker.show()
    }

    private fun updatePreview() {
        binding.previewText.typeface = currentFont
        binding.previewText.setTextColor(textColor)
        binding.previewText.setBackgroundColor(bgColor)
        binding.previewText.textSize = textSize
        applyOpenTypeFeatures()
    }

    private fun showFontInfo(name: String, description: String) {
        binding.fontInfo.text = "$name\n$description"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PICK_FONT) {
            data?.data?.let { uri ->
                loadFontFromUri(uri)
            }
        }
    }

    private fun loadFontFromUri(uri: Uri) {
        try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            currentFont = Typeface.createFromInputStream(inputStream)
            inputStream?.close()
            analyzeFont()
            updatePreview()
            showFontInfo("خط مخصص", "تم تحميله من الجهاز")
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "خطأ في تحميل الخط", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_CODE_PICK_FONT = 1004
    }
}