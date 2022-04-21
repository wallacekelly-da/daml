-- Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
-- SPDX-License-Identifier: Apache-2.0

{-# LANGUAGE CPP #-}

-- | Extended version of 'Test.Tasty.QuickCheck'.
module Test.Tasty.Golden.Extended (
  module Test.Tasty.Golden,
  GoldenTestTreeArgs (..),
  mkGoldenTestTree
) where

import Test.Tasty.Golden

import System.Directory (doesFileExist, listDirectory, makeAbsolute)
import System.FilePath (dropExtension, replaceExtensions, takeExtensions, (</>), (<.>))
import Data.List.Extra (nubOrd)
import Data.Text (Text)
import Test.Tasty.Extended (TestTree, testGroup)

import qualified Data.ByteString.Lazy as BSL
import qualified Data.Text.Encoding as TE

data GoldenTestTreeArgs = GoldenTestTreeArgs
  { testGroupName :: String
  , srcExtension :: String
  , expectedExtension :: String
  , goldenDir :: FilePath
  , mkOutput :: FilePath -> IO Text
  }

mkGoldenTestTree :: GoldenTestTreeArgs -> IO TestTree
mkGoldenTestTree GoldenTestTreeArgs {..} = do
  expectFiles <- filter isExpectationFile <$> listDirectory goldenDir

  let goldenSrcs = nubOrd $ map (flip replaceExtensions srcExtension) expectFiles

  goldenTests <- mapM (fileTest . (goldenDir </>)) goldenSrcs

  pure $ testGroup testGroupName $ concat goldenTests

  where
    isExpectationFile filePath =
      expectedExtension == takeExtensions (dropExtension filePath)

    fileTest = mkFileTest mkOutput expectedExtension

mkFileTest :: (FilePath -> IO Text) -> String -> FilePath -> IO [TestTree]
mkFileTest mkOutput expectedExtension srcFile = do
  srcFileAbs <- makeAbsolute srcFile
  let
    baseName = dropExtension srcFileAbs
    expectation = baseName <.> expectedExtension
  exists <- doesFileExist expectation

  if exists
    then do
      pure @IO . pure @[] $
        goldenVsStringDiff
          ("File: " <> expectation)
          diff
          expectation
          (BSL.fromStrict . TE.encodeUtf8 <$> mkOutput srcFile)

    else
      pure []

  where
    diff ref new = [POSIX_DIFF, "--strip-trailing-cr", ref, new]
