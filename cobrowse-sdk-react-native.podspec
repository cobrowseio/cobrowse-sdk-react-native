require "json"
package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name          = package['name']
  s.version       = package["version"]
  s.summary       = package['description']
  s.requires_arc  = true
  s.author        = { 'Andy' => 'andy@cobrowse.io' }
  s.license       = package["license"]
  s.homepage      = package["homepage"]
  s.source        = { :git => 'https://github.com/cobrowseio/cobrowse-sdk-react-native.git' }
  s.platform      = :ios, '11.0'
  s.dependency      'CobrowseIO/XCFramework', '3.2.0'
  s.dependency      'React'
  s.source_files =  'ios/*.{h,m}'
end
