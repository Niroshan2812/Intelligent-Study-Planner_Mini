import pandas as pd
import numpy as np
import xgboost as xgb
from sklearn.compose import ColumnTransformer
from sklearn.preprocessing import OneHotEncoder
from sklearn.pipeline import Pipeline
from skl2onnx import convert_sklearn, to_onnx
from skl2onnx.common.data_types import FloatTensorType, StringTensorType
import onnxmltools
from onnxmltools.convert import convert_xgboost
import joblib

# Load data
try:
    data = pd.read_csv('sri_lankan_student_data.csv')
except FileNotFoundError:
    data = pd.read_csv('ModelTraining/sri_lankan_student_data.csv')

# for define features
categorical_features = ['stream', 'district']
numerical_features = ['difficulty_level', 'current_score', 'english_fluency', 'tuition_hours_weekly', 'commute_fatigue']
target_column = 'hours_needed'

X = data[categorical_features + numerical_features]
y = data[target_column]

# Create Preprocessing Pipeline
preprocessor = ColumnTransformer(
    transformers=[
        ('cat', OneHotEncoder(handle_unknown='ignore', sparse_output=False), categorical_features),
        ('num', 'passthrough', numerical_features)
    ]
)

# Create the full pipeline
# In here it use the sklearn wrapper for XGBoost to make it compatible with sklearn Pipeline
model = Pipeline(steps=[
    ('preprocessor', preprocessor),
    ('regressor', xgb.XGBRegressor(n_estimators=100, max_depth=4, base_score=0.5))
])

# Train the model
print("Training model...")
model.fit(X, y)
print("Training Complete")
# Evaluate 
from sklearn.metrics import mean_squared_error, r2_score
predictions = model.predict(X)
rmse = np.sqrt(mean_squared_error(y, predictions))
r2 = r2_score(y, predictions)
print(f"Training RMSE: {rmse:.4f}")
print(f"Training R2: {r2:.4f}")

# Convert to ONNX
print("Converting to ONNX...")

# Register XGBoost converter for skl2onnx
from skl2onnx import update_registered_converter
from onnxmltools.convert.xgboost.operator_converters.XGBoost import convert_xgboost
from onnxmltools.convert.xgboost.shape_calculators.Regressor import calculate_linear_regressor_output_shapes

update_registered_converter(
    xgb.XGBRegressor, 
    'XGBoostXGBRegressor',
    calculate_linear_regressor_output_shapes, 
    convert_xgboost,
    options={'nocl': [True, False]}
)

from skl2onnx import to_onnx
onx = to_onnx(model, X[:1], target_opset={'': 12, 'ai.onnx.ml': 3})

# Save the model
output_file = "Study_predictor_improved.onnx"
with open(output_file, "wb") as f:
    f.write(onx.SerializeToString())

print(f"Model saved to '{output_file}'")
